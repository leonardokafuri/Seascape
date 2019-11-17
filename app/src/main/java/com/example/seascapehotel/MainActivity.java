package com.example.seascapehotel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      final  SharedPreferences preferences = this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);
      Button search = findViewById(R.id.signIn);
      final TextView date1 = findViewById(R.id.checkin);
      final TextView date2 = findViewById(R.id.checkout);
      final Spinner spinner = findViewById(R.id.guest);
        RelativeLayout checkin = findViewById(R.id.CheckinRel);
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.edit().putString("selected","checkin" ).apply();
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        RelativeLayout checkout = findViewById(R.id.CheckoutRel);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.edit().putString("selected","checkout" ).apply();
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });


        RelativeLayout signinregister = findViewById(R.id.signinregister);
        signinregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Login.class));
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date1.getText().toString().isEmpty() ||date2.getText().toString().isEmpty() )
                {
                    Toast.makeText(MainActivity.this,"Please select a date",Toast.LENGTH_SHORT).show();
                }else {
                    String guest = spinner.getSelectedItem().toString();
                    preferences.edit().putString("guests",guest).apply();
                    startActivity(new Intent(MainActivity.this,DisplayRooms.class));

                }
            }
        });

        RelativeLayout roomkey = findViewById(R.id.RoomKey);
        roomkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(preferences.getInt("CID",0)==0)
                {
                    Toast.makeText(MainActivity.this,"Please login to see your RoomKey",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startActivity(new Intent(MainActivity.this,DisplayBookings.class));
                }
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime()); // to display on the field
        String sharedprefdate = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()); // to save on the shared pref that will save on the dabtabase
        SharedPreferences preferences = this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);
        String btnclicked = preferences.getString("selected", "");
        if(btnclicked.contentEquals("checkin"))
        {
            TextView textView = (TextView) findViewById(R.id.checkin);
            preferences.edit().putString("checkin",sharedprefdate ).apply();
            textView.setText(currentDateString);
        }
        else if(btnclicked.contentEquals("checkout"))
        {
            TextView textView = (TextView) findViewById(R.id.checkout);
            preferences.edit().putString("checkout",sharedprefdate).apply();
            textView.setText(currentDateString);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.PastBookings:
                startActivity(new Intent(MainActivity.this,PastBookings.class));
                return true;
            case R.id.logout:
                startActivity(new Intent(MainActivity.this,Logout.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
