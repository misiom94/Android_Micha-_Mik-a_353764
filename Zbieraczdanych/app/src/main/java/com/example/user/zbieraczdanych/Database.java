package com.example.user.zbieraczdanych;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2016-12-29.
 */

public class Database extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION=1;
    public static final String TABLE_NAME="People";
    public static final String DATABASE_NAME="MyDatabasePeopeDB.db";
    public static final String C_ID="ID";
    public static final String C_NAME="name";
    public static final String C_SURNAME="surname";
    public static final String C_AGE="age";
    public static final String C_PATH="photo_path";


    public Database(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String NEW_TABLE="CREATE TABLE "+ TABLE_NAME +"("
                +C_ID+" integer primary key autoincrement,"
                +C_NAME+" TEXT,"
                +C_SURNAME+" TEXT,"
                +C_AGE+" TEXT,"
                +C_PATH+" TEXT);";
        sqLiteDatabase.execSQL(NEW_TABLE);

    }
    public void addPerson(String name,String surname,String age,String path)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(C_NAME,name);
        values.put(C_SURNAME,surname);
        values.put(C_AGE,age);
        values.put(C_PATH,path);
        sqLiteDatabase.insertOrThrow(TABLE_NAME,null,values);
    }
    public void editPerson(int id,String name,String surname,String age,String path)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(C_NAME,name);
        values.put(C_SURNAME,surname);
        values.put(C_AGE,age);
        values.put(C_PATH,path);
        String args[]={""+id};
        sqLiteDatabase.update(TABLE_NAME,values,"ID=?",args);

    }

    public void deletePerson(Integer id)
    {
        SQLiteDatabase sqLiteDatabase =getWritableDatabase();
        String args[]={""+id};
        sqLiteDatabase.delete(TABLE_NAME,"ID=?",args);
    }
    public Cursor getData()
    {
        String[] columns={C_ID,C_NAME,C_SURNAME,C_AGE,C_PATH};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,columns,null,null,null,null,null);
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
