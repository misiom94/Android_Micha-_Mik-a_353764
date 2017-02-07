package com.example.user.zbieraczdanych;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class SummaryActivity extends AppCompatActivity {

    TextView textViewName;
    TextView textViewSurname;
    TextView textViewBirthdayDate;
    ImageView imageViewPhoto;
    final static String EXTRA_NAME = "extra_name";
    final static String EXTRA_SURNAME = "extra_surname";
    final static String EXTRA_BIRTHDAY_DATE = "extra_birthday_date";
    final static String EXTRA_PHOTO_ABSOLUTE_PATH = "extra_photo_absolute_path";
    private final static String INFO_WHAT_TO_DO = "info_what_to_do";
    private final static String EDIT_ID = "edit_id";
    private final static String EDIT_PHOTO_PATH = "edit_photo_path";
    String photoAbsolutePath;
    Button buttonStartOver;
    Intent intentAddPersonPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        setContentView(R.layout.activity_summary);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewSurname = (TextView) findViewById(R.id.textViewSurname);
        textViewBirthdayDate = (TextView) findViewById(R.id.textViewBirthday);
        imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
        buttonStartOver = (Button) findViewById(R.id.buttonStartOver);

        intentAddPersonPhoto = getIntent();
        textViewName.setText(intentAddPersonPhoto.getStringExtra(EXTRA_NAME));
        textViewSurname.setText(intentAddPersonPhoto.getStringExtra(EXTRA_SURNAME));
        textViewBirthdayDate.setText(intentAddPersonPhoto.getStringExtra(EXTRA_BIRTHDAY_DATE));
        photoAbsolutePath = intentAddPersonPhoto.getStringExtra(EXTRA_PHOTO_ABSOLUTE_PATH);
        showPicture();
        if(intentAddPersonPhoto.getStringExtra(INFO_WHAT_TO_DO).equals("EDIT")){
            buttonStartOver.setEnabled(false);
        }
    }
    public void startOver(View view){
        Intent intentPersonEdit = new Intent(this,Person_Add.class);
        File filePhoto = new File(photoAbsolutePath);
        filePhoto.delete();
        startActivity(intentPersonEdit);
        finish();
    }

    public void clickExit(View view){
        deletePhoto(photoAbsolutePath);
        finish();
    }

    public void addOrUpdatePersonToDatabase(View view){
        Database database = new Database(this);
        if(intentAddPersonPhoto.getStringExtra(INFO_WHAT_TO_DO).equals("EDIT")){
            deletePhoto(intentAddPersonPhoto.getStringExtra(EDIT_PHOTO_PATH));
            database.editPerson(intentAddPersonPhoto.getIntExtra(EDIT_ID,0),intentAddPersonPhoto.getStringExtra(EXTRA_NAME),
                    intentAddPersonPhoto.getStringExtra(EXTRA_SURNAME),intentAddPersonPhoto.getStringExtra(EXTRA_BIRTHDAY_DATE),
                    intentAddPersonPhoto.getStringExtra(EXTRA_PHOTO_ABSOLUTE_PATH));
        }
        else{
            database.addPerson(textViewName.getText().toString(), textViewSurname.getText().toString(),
                    textViewBirthdayDate.getText().toString(),photoAbsolutePath);
        }
        finish();
    }

    private void deletePhoto(String photoFile){
        if(intentAddPersonPhoto.getStringExtra(INFO_WHAT_TO_DO).equals("EDIT")){
            if(!photoFile.equals(intentAddPersonPhoto.getStringExtra(EDIT_PHOTO_PATH))){
                File filePhoto = new File(photoAbsolutePath);
                filePhoto.delete();
                Log.i("New file","Deleted");
            }
        }
    }

    private void showPicture(){
        if(!photoAbsolutePath.equals("")){
            Matrix deviceMatrix = new Matrix();

            ExifInterface photoInterface = null;
            try {
                photoInterface = new ExifInterface(photoAbsolutePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int photoOrientation = photoInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,5);
            Log.i("PhotoOrientation",photoOrientation+"");
            if(photoOrientation==3){
                deviceMatrix.postRotate(180);
            }
            else if(photoOrientation!=1){
                deviceMatrix.postRotate(90);
            }
            int smallerScreenLength = whatIsSmallerHeightOrWidth();
            Log.i("Length",smallerScreenLength+"");
            Bitmap bitmapPicture = BitmapFactory.decodeFile(photoAbsolutePath);
            deviceMatrix.postScale((float)smallerScreenLength/bitmapPicture.getWidth(),(float)smallerScreenLength/bitmapPicture.getWidth());
            Bitmap processedBitmap = Bitmap.createBitmap(bitmapPicture , 0, 0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), deviceMatrix, true);
            imageViewPhoto.setImageBitmap(processedBitmap);
        }
    }
    private int whatIsSmallerHeightOrWidth(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceHeight = displayMetrics.heightPixels;
        int deviceWidth = displayMetrics.widthPixels;
        if(deviceHeight>deviceWidth){
            return  deviceWidth;
        }
        return deviceHeight;
    }


}
