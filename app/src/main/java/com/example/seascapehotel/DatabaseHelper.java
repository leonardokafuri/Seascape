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

        // create the other tables for caching later on

        try {
            db.execSQL(queryPasswordReset);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists PasswordReset");
        onCreate(db);
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

    public void resetPassword(String email, String code, String password) throws Exception {
        //get users by email on online db
        /*
        cUser.moveToFirst();
        Integer userId = cUser.getInt(0);
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM PasswordReset WHERE UserId = " + userId.toString() + " Order by CreatedTime desc";
        Cursor c = sqLiteDatabase.rawQuery(query,null);
        if(c.getCount() == 0)
            throw new Exception("There is no reset token for this user! Please fill your email and hit send code!");
        c.moveToFirst();
        if(!code.equals(c.getString(2)))
            throw new Exception("This code is invalid, please check your emails for the latest sent code");

        SQLiteDatabase db2 = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        password = get_SHA_256_SecurePassword(password);

        cv.put("Password",password);
       //update user with new password on online db


         */

    }



   //use below if we have time to hash the password
    /*
   private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static String get_SHA_256_SecurePassword(String passwordToHash)
    {
        String generatedPassword = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    passwordToHash.getBytes());
            generatedPassword = bytesToHex(encodedhash);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }*/

}



