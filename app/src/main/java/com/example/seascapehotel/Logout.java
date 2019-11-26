package com.example.seascapehotel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Logout extends AppCompatActivity {
    DatabaseHelper dbh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        final SharedPreferences preferences = this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);
        TextView log = findViewById(R.id.logout);
        Button main = findViewById(R.id.backmain);
        log.append("You have been logged out!");
        preferences.edit().putInt("CID",0).apply();
        preferences.edit().putString("lgfname","-").apply();
        preferences.edit().putString("bkci","-").apply();
        dbh = new DatabaseHelper(this);
        try{
            dbh.onDelete();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Logout.this,MainActivity.class));
            }
        });



    }
}
