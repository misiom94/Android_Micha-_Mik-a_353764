package com.example.user.zbieraczdanych;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Person_Add extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText textName;
    EditText textSurname;
    TextView textBirthday;
    private int currentlyDay, currentlyMonth, currentlyYear;
    private int pickedDay, pickedMonth, pickedYear;

    private final static String EXTRA_NAME = "extra_name";
    private final static String EXTRA_SURNAME = "extra_surname";
    private final static String EXTRA_BIRTHDAY_DATE = "extra_birthday_date";

    private final static String SAVE_NAME = "saved_name";
    private final static String SAVE_SURNAME = "saved_surname";
    private final static String SAVE_BIRTHDAY = "saved_birthday";
    private final static String SAVE_PICKED_YEAR = "saved_picked_year";
    private final static String SAVE_PICKED_MONTH = "saved_picked_month";
    private final static String SAVE_PICKED_DAY = "saved_picked_day";

    private final static String INFO_WHAT_TO_DO = "info_what_to_do";
    private final static String EDIT_ID = "edit_id";
    private final static String EDIT_NAME = "edit_name";
    private final static String EDIT_SURNAME = "edit_surname";
    private final static String EDIT_BIRTHDAY = "edit_birthday";
    private final static String EDIT_PHOTO_PATH = "edit_photo_path";
    Intent intentMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person__add);
        textName = (EditText)findViewById(R.id.editTextName);
        textSurname = (EditText)findViewById(R.id.editTextSurname);
        textBirthday = (TextView)findViewById(R.id.textViewAge);
        setCurrentlyDate();
        intentMainActivity = getIntent();
        checkIfEditPerson(intentMainActivity);


    }

    private void checkIfEditPerson(Intent intentMainActivity){
        if((intentMainActivity.getStringExtra(INFO_WHAT_TO_DO)).equals("EDIT")){
            textName.setText(intentMainActivity.getStringExtra(EDIT_NAME));
            textSurname.setText(intentMainActivity.getStringExtra(EDIT_SURNAME));
            textBirthday.setText(intentMainActivity.getStringExtra(EDIT_BIRTHDAY));
            StringBuffer birthDayDateBuffer = new StringBuffer(intentMainActivity.getStringExtra(EDIT_BIRTHDAY));
            pickedYear = setPickedYearAndCutFirstSlash(birthDayDateBuffer);
            pickedMonth = setPickedMonthAndCutSecondSlash(birthDayDateBuffer);
            pickedDay = setPickedDay(birthDayDateBuffer);
        }
    }

    private int setPickedYearAndCutFirstSlash(StringBuffer birthDayDateBuffer){
        String birthdayYear = birthDayDateBuffer.substring(0,4);
        birthDayDateBuffer.delete(0,5);
        return Integer.parseInt(birthdayYear);
    }

    private int setPickedMonthAndCutSecondSlash(StringBuffer birthDayDateBuffer){
        StringBuffer birthdayMonth = new StringBuffer();
        birthdayMonth.append(birthDayDateBuffer.charAt(0));
        birthDayDateBuffer.deleteCharAt(0);
        if(birthDayDateBuffer.charAt(0) != '/'){
            birthdayMonth.append(birthDayDateBuffer.charAt(0));
            birthDayDateBuffer.deleteCharAt(0);
        }
        birthDayDateBuffer.deleteCharAt(0);
        return Integer.parseInt(birthdayMonth.toString());
    }

    private int setPickedDay(StringBuffer birthDayDateBuffer){
        String birthdayDay = birthDayDateBuffer.substring(0,birthDayDateBuffer.length());
        return Integer.parseInt(birthdayDay);
    }

    public void toPhoto(View view){
        if(checkPersonDetails(textName.getText().toString()
                , textSurname.getText().toString()
                , textBirthday.getText().toString())){
            Intent intentPhotoActivity = new Intent(this,Photo_Add.class);
            intentPhotoActivity.putExtra(INFO_WHAT_TO_DO,intentMainActivity.getStringExtra(INFO_WHAT_TO_DO));
            intentPhotoActivity.putExtra(EXTRA_NAME,textName.getText().toString());
            intentPhotoActivity.putExtra(EXTRA_SURNAME,textSurname.getText().toString());
            intentPhotoActivity.putExtra(EXTRA_BIRTHDAY_DATE,textBirthday.getText().toString());

            if(intentMainActivity.getStringExtra(INFO_WHAT_TO_DO).equals("EDIT")){
                intentPhotoActivity.putExtra(EDIT_ID,intentMainActivity.getIntExtra(EDIT_ID,0));
                intentPhotoActivity.putExtra(EDIT_PHOTO_PATH,intentMainActivity.getStringExtra(EDIT_PHOTO_PATH));
            }

            startActivity(intentPhotoActivity);
            finish();
        }
    }

    private boolean checkPersonDetails(String name, String surname, String birthday){
        int controlCountDetails = 0;
        if(!name.equals("") && name.length() <= 20){
            controlCountDetails++;
        }
        if(!surname.equals("") && surname.length() <= 20){
            controlCountDetails++;
        }
        if(checkBirthdayIsBeforeToday(birthday)){
            controlCountDetails++;
        }
        if(controlCountDetails==3){
            return true;
        }
        return false;
    }

    private boolean checkBirthdayIsBeforeToday(String birthday){
        if(!birthday.equals("")){

            int controlCountBirthday=0;

            if(checkBirthdayYear()){
                controlCountBirthday++;
            }
            if(checkBirthdayMonth()){
                controlCountBirthday++;
            }
            if(checkBirthdayDay()){
                controlCountBirthday++;
            }
            if(controlCountBirthday==3){
                return true;
            }

        }
        return false;
    }

    private boolean checkBirthdayMonth(){
        if(pickedYear==currentlyYear){
            if(pickedMonth>currentlyMonth+1){
                return false;
            }
        }
        return true;
    }

    private boolean checkBirthdayDay(){
        if(checkBirthdayMonth()){
            if(pickedYear==currentlyYear && pickedMonth==currentlyMonth+1){
                if(pickedDay> currentlyDay){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkBirthdayYear(){
        if(checkBirthdayMonth()){
            if(pickedYear>currentlyYear ){
                return false;
            }
            if(currentlyYear-pickedYear>120){
                return false;
            }
        }
        return true;
    }

    private void setCurrentlyDate(){
        Calendar calendar = Calendar.getInstance();
        currentlyYear = calendar.get(Calendar.YEAR);
        currentlyMonth = calendar.get(Calendar.MONTH);
        currentlyDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void pickBirthdayDate(View view){
        if(textBirthday.getText().toString().equals(getString(R.string.view_birth))){
            DatePickerDialog datePickerDialogTakeBirthday = new DatePickerDialog(this,this, currentlyYear, currentlyMonth, currentlyDay);
            datePickerDialogTakeBirthday.show();
        }
        else{
            DatePickerDialog datePickerDialogTakeBirthday = new DatePickerDialog(this,this, pickedYear, pickedMonth-1, pickedDay);
            datePickerDialogTakeBirthday.show();
        }
    }
    public void clickExit(View view){
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVE_NAME,textName.getText().toString());
        outState.putString(SAVE_SURNAME,textSurname.getText().toString());
        outState.putString(SAVE_BIRTHDAY,textBirthday.getText().toString());
        outState.putInt(SAVE_PICKED_YEAR,pickedYear);
        outState.putInt(SAVE_PICKED_MONTH,pickedMonth);
        outState.putInt(SAVE_PICKED_DAY,pickedDay);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        textName.setText(savedInstanceState.getString(SAVE_NAME));
        textSurname.setText(savedInstanceState.getString(SAVE_SURNAME));
        textBirthday.setText(savedInstanceState.getString(SAVE_BIRTHDAY));
        pickedYear = savedInstanceState.getInt(SAVE_PICKED_YEAR);
        pickedMonth = savedInstanceState.getInt(SAVE_PICKED_MONTH);
        pickedDay = savedInstanceState.getInt(SAVE_PICKED_DAY);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        pickedYear = year;
        pickedMonth = month + 1;
        pickedDay = day;

        textBirthday.setText(pickedYear + "/" + pickedMonth + "/" + pickedDay);
    }
}
