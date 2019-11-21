package com.example.seascapehotel;

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
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


public class SuccessfulPayment extends AppCompatActivity {
    ProgressDialog pd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_payment);

        new JsonTask().execute("http://ec2-54-196-138-183.compute-1.amazonaws.com/hotel/BookRoom.php");

        Button back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SuccessfulPayment.this,MainActivity.class));
            }
        });
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        final SharedPreferences preferences =SuccessfulPayment.this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SuccessfulPayment.this);
            pd.setMessage("Booking your room ...");
            pd.setCancelable(false);
            pd.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            int cid = preferences.getInt("CID",0);
            String CustomerID = String.valueOf(cid);
            String RoomID = preferences.getString("RID",""); // getting the RoomID
            String Checkin = preferences.getString("checkin",""); // getting days
            String Checkout = preferences.getString("checkout",""); // getting days
            String price = preferences.getString("RPrice",""); //getting price
            //calculating number of days in between
            LocalDate dateBefore = LocalDate.parse(Checkin);
            LocalDate dateAfter = LocalDate.parse(Checkout);
            long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
            // calculation done for num of days
            double p = Double.parseDouble(price);
            double T = (int)noOfDaysBetween * p ;
            String Nights = String.valueOf(noOfDaysBetween); // Amount of nights
            String Total = String.valueOf(T); // total value
            preferences.edit().putString("NumNights",Nights).apply();
            preferences.edit().putString("Btotal",Total).apply();
            try {
                String data = URLEncoder.encode("CustomerID","UTF-8")+ "=" +URLEncoder.encode(CustomerID,"UTF-8");
                data += "&" + URLEncoder.encode("RoomID","UTF-8")+ "="+ URLEncoder.encode(RoomID,"UTF-8");
                data += "&" + URLEncoder.encode("Checkin","UTF-8")+ "="+ URLEncoder.encode(Checkin,"UTF-8");
                data += "&" + URLEncoder.encode("Checkout","UTF-8")+ "="+ URLEncoder.encode(Checkout,"UTF-8");
                data += "&" + URLEncoder.encode("Total","UTF-8")+ "="+ URLEncoder.encode(Total,"UTF-8");
                data += "&" + URLEncoder.encode("Nights","UTF-8")+ "="+ URLEncoder.encode(Nights,"UTF-8");
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

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            try {
                String Checkin = preferences.getString("checkin", ""); // getting days
                String Checkout = preferences.getString("checkout", ""); // getting days
                String total = preferences.getString("Btotal", "");
                String Nights = preferences.getString("NumNights", "");
                if (result.contentEquals("Room Booked")) {
                    TextView info = findViewById(R.id.BookingInfo);
                    info.append("Your Room has been booked, here is the info : \n");
                    info.append("Check-in: " + Checkin + "\n");
                    info.append("Check-out " + Checkout + "\n");
                    info.append("Nights: " + Nights + "\n");
                    info.append("Total: " + total + "\n");

                } else {
                    TextView info = findViewById(R.id.BookingInfo);
                    info.append("Something went wrong trying to book your room, please try again");
                }
            }catch (Exception e)
            {
                Toast.makeText(SuccessfulPayment.this,"Something went wrong on our side, please try again later",Toast.LENGTH_LONG).show();
            }
        }

    }


}
