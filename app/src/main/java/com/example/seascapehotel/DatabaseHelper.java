package com.example.seascapehotel;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {

    final static String DATABASE_NAME = "SeaScape.db";
    final static int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryPasswordReset = "Create Table PasswordReset(ResetId INTEGER PRIMARY KEY," +
                "UserId INTEGER, PasswordToken TEXT, CreatedTime INTEGER)";

        String createCachingTable = "Create Table Reservation(RoomID INTEGER," +
                " Checkin DATE, " +
                "Checkout DATE , Description TEXT , Email TEXT, Name TEXT, Total DOUBLE )";

        // create the other tables for caching later on

        try {
            db.execSQL(queryPasswordReset);
            db.execSQL(createCachingTable);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists PasswordReset");
        db.execSQL("Drop table if exists Reservation");
        onCreate(db);
    }
    public void onDelete(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Reservation");
    }


    public String createResetToken(Integer UserId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UserId", UserId);
        //Generates random token
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        values.put("PasswordToken", uuid);
        long now = System.currentTimeMillis();
        values.put("CreatedTime", now);

        long r = db.insert("PasswordReset", null, values);
        if (r == -1) {
            return "";
        } else {
            return uuid;
        }
    }
    public boolean passData(int r, String d1, String d2, String d, String e, String n, double t){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("RoomID",r);
        values.put("Checkin",d1);
        values.put("Checkout",d2);
        values.put("Total",t);
        values.put("Description",d);
        values.put("Email",e);
        values.put("Name",n);
        long ins = db.insert("Reservation",null,values);
        if(ins == -1){
            return  false;
        }
        else{
            return true;
        }
    }

    public Cursor localData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Reservation";
        Cursor c = db.rawQuery(query,null);
        return c;
    }

}