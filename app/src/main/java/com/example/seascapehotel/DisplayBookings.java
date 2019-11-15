package com.example.seascapehotel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;

public class DisplayBookings extends AppCompatActivity {
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_bookings);
        new JsonTask().execute("http://10.0.2.2:8888/MAMP/hotel/PastBookings.php");
    }


    private class JsonTask extends AsyncTask<String, String, String> {
        final SharedPreferences preferences =DisplayBookings.this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(DisplayBookings.this);
            pd.setMessage("Searching for Bookings...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            int id = preferences.getInt("CID",0);
            String userid = String.valueOf(id);
            Log.d("response",userid);
            try {
                String data = URLEncoder.encode("CustomerID","UTF-8")+ "=" +URLEncoder.encode(userid,"UTF-8");
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
            if(result.equals("nothing"))
            {
                LinearLayout layout = findViewById(R.id.lv);
                TextView tv;
                tv= new TextView(DisplayBookings.this);
                tv.append("No bookings made to genereate a key");
                layout.addView(tv);

            }else
            {
                final ArrayList<BookingData> p = proccessData(result);
                if(p != null)
                {
                    LinearLayout layout = findViewById(R.id.lv);
                    TextView tv;
                    Button btn;
                    for(final BookingData elem : p)
                    {
                        tv= new TextView(DisplayBookings.this);
                        tv.append(elem.Name +"\n");
                        tv.append(elem.Description + "\n");
                        tv.append("$ "+ elem.Total + "\n");
                        tv.append("Checkin:"+elem.Checkin + "       Checkout:" + elem.Checkout + "\n");
                        tv.append(elem.Email);
                        btn = new Button(DisplayBookings.this);
                        btn.setBackgroundResource(R.color.blue);
                        btn.setTextColor(Color.WHITE);
                        btn.setText("Generate Room Key");
                        layout.addView(tv);
                        layout.addView(btn);

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // if booked room has been choosen
                                //save checkin, checkout in shared pref for qr
                                String checkin =elem.Checkin;
                                String checkout = elem.Checkout;
                                preferences.edit().putString("bkci",checkin).apply();
                                preferences.edit().putString("bkco",checkout).apply();
                                //start Qr activity
                                startActivity(new Intent(DisplayBookings.this,RoomKey.class));
                            }
                        });
                    }
                }
            }
        }

    }
    private ArrayList<BookingData> proccessData(String data)
    {
        ArrayList<BookingData> temp = new ArrayList<>();
        try{
            JSONArray ar = new JSONArray(data);
            JSONObject element;
            BookingData bd;
            for(int i=0; i<ar.length();i++)
            {
                element = ar.getJSONObject(i);
                bd = new BookingData();
                bd.Name = element.getString("Name");
                bd.Checkin=element.getString("Checkin");
                bd.Checkout=element.getString("Checkout");
                bd.Description = element.getString("Description");
                bd.Email=element.getString("Email");
                bd.Total = element.getString("Total");

                temp.add(bd);
            }
            return temp;

        }catch (JSONException E)
        {
            Log.d("MainActivity",E.getMessage());
        }
        return null;
    }
}
