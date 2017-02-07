package com.example.user.zbieraczdanych;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    String language;
    Button buttonPolish;
    Button buttonBack;
    Button buttonEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        buttonPolish = (Button) findViewById(R.id.buttonPolish);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonEnglish = (Button) findViewById(R.id.buttonEnglish);
    }

    public void clickChangeLanguageToPolish(View view){
        language = "pl";
        updateLocale();
        buttonPolish.setText(getString(R.string.button_polish));
        buttonBack.setText(getString(R.string.button_back));
        buttonEnglish.setText(getString(R.string.button_english));
    }

    public void clickChangeLanguageToEnglish(View view) {
        language = "default";
        updateLocale();
        buttonPolish.setText(getString(R.string.button_polish));
        buttonBack.setText(getString(R.string.button_back));
        buttonEnglish.setText(getString(R.string.button_english));
    }

    private void updateLocale()
    {
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void backToMainActivity(View view){
        Intent intentMainActivity = new Intent(this,MainActivity.class);
        startActivity(intentMainActivity);
        finish();
    }
}
