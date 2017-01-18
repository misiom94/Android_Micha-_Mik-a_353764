package com.example.user.gpsappapi18;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener, OnMapReadyCallback {

    private SensorManager sensorManager;
    private Sensor lightSensor, gyroSensor, rotationSensor;
    private Button cameraButton, mapButton;
    private TextView lightView, gyroView, rotationView, locationView;
    private ImageView arrowImage;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private String gpsProvider;
    private double latitude;
    private double longitude;
    private Location location;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private float currentDegree = 0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    Camera camera;
    Camera.Parameters parameters;

    private boolean lightFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        //<<----WYSWIETLACZE------>
        lightView = (TextView) findViewById(R.id.lightText);
        lightView.setText("Light Pressure");
        gyroView = (TextView) findViewById(R.id.gyroText);
        gyroView.setText("Gyroscope: ");
        rotationView = (TextView) findViewById(R.id.rotationText);
        rotationView.setText("Rotation: ");
        arrowImage = (ImageView) findViewById(R.id.arrowView);
        cameraButton = (Button) findViewById(R.id.flashOn);
        mapButton = (Button) findViewById(R.id.mapButton);
        locationView = (TextView) findViewById(R.id.locationText);
        locationView.setText("Location: ");


        //<-----DEKLARACJA SENSOROW------>
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);


        //<----KAMERA---->
        boolean hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (hasFlash == true) {
            camera = Camera.open();
            parameters = camera.getParameters();

        }
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lightFlag == true) {
                    cameraButton.setText("ON");
                    lightFlag = false;
                    stopFlash();
                } else {
                    cameraButton.setText("OFF");
                    lightFlag = true;
                    startFlash();
                }
            }
        });

        //<----Lokalizacja----->
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsProvider = locationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(gpsProvider);
        locationManager.requestLocationUpdates(gpsProvider, 1000, 1, this);
        try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            locationView.setText("Lat: " + latitude + " \n Lon: " + longitude);
        } catch (NullPointerException e) {
            Toast.makeText(MainActivity.this, "No location found", Toast.LENGTH_SHORT);
        }


    }

    public void startFlash() {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
        camera.startPreview();
    }

    public void stopFlash() {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
        camera.stopPreview();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor checkSensor = event.sensor;

        if (checkSensor.getType() == Sensor.TYPE_LIGHT) {
            float lightPressure = event.values[0];
            lightView.setText("Light: " + lightPressure);

        }
        if (checkSensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];
                float omegaMagnitude = (float) sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
                if (omegaMagnitude > 0.0001) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                float sinThetaOverTwo = (float) sin(thetaOverTwo);
                float cosThetaOverTwo = (float) cos(thetaOverTwo);
                deltaRotationVector[0] = sinThetaOverTwo * axisX;
                deltaRotationVector[1] = sinThetaOverTwo * axisY;
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                deltaRotationVector[3] = cosThetaOverTwo;
                gyroView.setText("GYROSCOPE: " + "\n X : " + axisX + "\n Y: " + axisY + "\n Z: " + axisZ);
            }
            timestamp = event.timestamp;
            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);

        }
        if (checkSensor.getType() == Sensor.TYPE_ORIENTATION) {
            int degree = Math.round(event.values[0]);
            rotationView.setText(Integer.toString(degree) + (char) 0x00B0);
            RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(1000);
            ra.setFillAfter(true);
            arrowImage.startAnimation(ra);
            currentDegree = -degree;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onLocationChanged(Location location) {




    }

    @Override
    protected void onResume() {
        super.onResume();
        //<-----REJESTRACJA SENSOROW---->
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(MainActivity.this, "Light Not supported!", Toast.LENGTH_SHORT).show();
        }
        if (gyroSensor != null) {
            sensorManager.registerListener(this, gyroSensor, sensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(MainActivity.this, "Gyroscope Not supported!", Toast.LENGTH_SHORT).show();
        }
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, sensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(MainActivity.this, "Orientation Not supported", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void startMap(View v) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Long", longitude);
        intent.putExtra("Lat", latitude);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        this.mMap=googleMap;

    }
}
