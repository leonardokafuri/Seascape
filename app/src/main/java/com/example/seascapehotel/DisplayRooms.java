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

public class DisplayRooms extends AppCompatActivity {
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_rooms);

        new JsonTask().execute("http://10.0.2.2:8888/MAMP/hotel/SearchRoom.php");
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        final SharedPreferences preferences =DisplayRooms.this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(DisplayRooms.this);
            pd.setMessage("Searching for rooms...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String date1 = preferences.getString("checkin","");
            String date2 = preferences.getString("checkout","");
            String guests = preferences.getString("guests","");
            Log.d("aa",guests);
            try {
                String data = URLEncoder.encode("checkin","UTF-8")+ "=" +URLEncoder.encode(date1,"UTF-8");
                data += "&" + URLEncoder.encode("checkout","UTF-8")+ "="+ URLEncoder.encode(date2,"UTF-8");
                data += "&" + URLEncoder.encode("guests","UTF-8")+ "="+ URLEncoder.encode(guests,"UTF-8");
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
                        buffer.append(line + "\n");
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
            if(result.equals("0 results"))
            {
                // error toast
            }else
            {
                final ArrayList<RoomData> p = proccessData(result);
                if(p != null)
                {
                    LinearLayout layout = findViewById(R.id.lv);
                    ImageView iv;
                    TextView tv;
                    Button btn;
                    for(final RoomData elem : p)
                    {
                        iv = new ImageView(DisplayRooms.this);
                        Picasso.with(DisplayRooms.this).load(elem.Picture).into(iv);
                        tv= new TextView(DisplayRooms.this);
                        tv.append(elem.Name +"\n");
                        tv.append(elem.Description + "\n");
                        tv.append("$"+elem.Price+ " Per Night");
                        btn = new Button(DisplayRooms.this);
                        btn.setBackgroundResource(R.color.blue);
                        btn.setTextColor(Color.WHITE);
                        btn.setText("Book Room");
                        layout.addView(iv);
                        layout.addView(tv);
                        layout.addView(btn);

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // if room has been choosen
                                //check for login before proceed to payment
                                String price= elem.Price;
                                startActivity(new Intent(DisplayRooms.this,Payment.class));
                                // save the data and put on shared pref
                                //start payment activity
                            }
                        });
                    }
                }
            }
        }

    }

    private ArrayList<RoomData> proccessData(String data)
    {
        ArrayList<RoomData> temp = new ArrayList<>();
        try{
            JSONArray ar = new JSONArray(data);
            JSONObject element;
            RoomData rd;
            for(int i=0; i<ar.length();i++)
            {
                element = ar.getJSONObject(i);
                rd = new RoomData();
                rd.Name = element.getString("Name");
                rd.Description = element.getString("Description");
                rd.Price = element.getString("Price");
                rd.Picture = element.getString("Picture");
                temp.add(rd);
            }
            return temp;

        }catch (JSONException E)
        {
            Log.d("MainActivity",E.getMessage());
        }
        return null;
    }
}
