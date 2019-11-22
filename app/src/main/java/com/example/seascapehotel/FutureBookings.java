package com.example.seascapehotel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FutureBookings extends AppCompatActivity {
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_bookings);
        new JsonTask().execute("http://10.0.2.2:8888/MAMP/hotel/FutureBookings.php");
    }


    private class JsonTask extends AsyncTask<String, String, String> {
        final SharedPreferences preferences =FutureBookings.this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(FutureBookings.this);
            pd.setMessage("Searching for Bookings...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            Calendar c = Calendar.getInstance();
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            int id = preferences.getInt("CID",0);
            String userid = String.valueOf(id);
            String currentday = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
            try {
                String data = URLEncoder.encode("CustomerID","UTF-8")+ "=" +URLEncoder.encode(userid,"UTF-8");
                data += "&" + URLEncoder.encode("currentday","UTF-8")+ "="+ URLEncoder.encode(currentday,"UTF-8");
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
            try{
                if(result.equals("nothing"))
                {
                    LinearLayout layout = findViewById(R.id.lv6);
                    TextView tv;
                    tv= new TextView(FutureBookings.this);
                    tv.append("No bookings available to be deleted");
                    layout.addView(tv);

                }else
                {
                    final ArrayList<FutureBookingData> p = proccessData(result);
                    if(p != null)
                    {
                        LinearLayout layout = findViewById(R.id.lv6);
                        TextView tv;
                        Button btn;
                        for(final FutureBookingData elem : p)
                        {
                            tv= new TextView(FutureBookings.this);
                            tv.append(elem.Name +"\n");
                            tv.append(elem.Description + "\n");
                            tv.append("$"+ elem.Total + "\n");
                            tv.append("Checkin:"+elem.Checkin + "       Checkout:" + elem.Checkout + "\n");
                            tv.append(elem.Email);
                            btn = new Button(FutureBookings.this);
                            btn.setBackgroundResource(R.color.blue);
                            btn.setTextColor(Color.WHITE);
                            btn.setText("Delete Booking");
                            layout.addView(tv);
                            layout.addView(btn);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // if booked room has been choosen
                                    //save checkin, checkout in shared pref for deletion
                                    String checkin =elem.Checkin;
                                    String checkout = elem.Checkout;
                                    String ResID = elem.ReservationID;
                                    preferences.edit().putString("dlci",checkin).apply();
                                    preferences.edit().putString("dlco",checkout).apply();
                                    preferences.edit().putString("dlresid",ResID).apply();
                                    preferences.edit().putString("dltotal",elem.Total).apply();
                                    preferences.edit().putString("dlemail", elem.Email).apply();
                                    //start deletion activity
                                    startActivity(new Intent(FutureBookings.this,SuccessfulDeletion.class));
                                }
                            });
                        }
                    }
                }

            }catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(FutureBookings.this,"Please check your internet connection",Toast.LENGTH_LONG).show();
            }
        }

    }
    private ArrayList<FutureBookingData> proccessData(String data)
    {
        ArrayList<FutureBookingData> temp = new ArrayList<>();
        try{
            JSONArray ar = new JSONArray(data);
            JSONObject element;
            FutureBookingData fbd;
            for(int i=0; i<ar.length();i++)
            {
                element = ar.getJSONObject(i);
                fbd = new FutureBookingData();
                fbd.Name = element.getString("Name");
                fbd.Checkin=element.getString("Checkin");
                fbd.Checkout=element.getString("Checkout");
                fbd.Description = element.getString("Description");
                fbd.Email=element.getString("Email");
                fbd.Total = element.getString("Total");
                fbd.ReservationID = element.getString("ReservationID");
                temp.add(fbd);
            }
            return temp;

        }catch (JSONException E)
        {
            Log.d("MainActivity",E.getMessage());
        }
        return null;
    }
}
