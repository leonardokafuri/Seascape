package com.example.seascapehotel;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SuccessfulDeletion extends AppCompatActivity {
    ProgressDialog pd;
    String URL = API.URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_deletion);

        new JsonTask().execute(URL+"deletebooking.php");
        Button back  = findViewById(R.id.backButtonmainpg);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SuccessfulDeletion.this,MainActivity.class));
            }
        });

    }

    private class JsonTask extends AsyncTask<String, String, String> {
        final SharedPreferences preferences =SuccessfulDeletion.this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SuccessfulDeletion.this);
            pd.setMessage("Booking your room ...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String ReservationID = preferences.getString("dlresid","");
            try {
                String data = URLEncoder.encode("ReservationID","UTF-8")+ "=" +URLEncoder.encode(ReservationID,"UTF-8");
                try {
                    URL url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                        Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                    }

                    return buffer.toString();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            try {
                String Checkin = preferences.getString("dlci", ""); // getting days
                String Checkout = preferences.getString("dlco", ""); // getting days
                String Email = preferences.getString("dlemail", "");
                String total = preferences.getString("dltotal", "");
                String Res = preferences.getString("dlresid", "");
                if (result.contentEquals("deleted")) {
                    TextView info = findViewById(R.id.BookingdelInfo);
                    info.append("Your booking has been canceled, here is the info : \n");
                    info.append("Reservation ID :" + Res +'\n' );
                    info.append("Check-in: " + Checkin + "\n");
                    info.append("Check-out " + Checkout + "\n");
                    info.append("Total to be refunded : $"+total + "\n");
                    info.append("Email : " + Email);
                } else {
                    TextView info = findViewById(R.id.BookingInfo);
                    info.append("Something went wrong trying to cancel your booking, please try again");
                }
            }catch (Exception e)
            {
                Toast.makeText(SuccessfulDeletion.this,"Something went wrong on our side, please try again later",Toast.LENGTH_LONG).show();
            }
        }

    }
}
