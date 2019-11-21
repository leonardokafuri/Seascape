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
import java.util.ArrayList;

public class ViewReviews extends AppCompatActivity {
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviews);
        new JsonTask().execute("http://10.0.2.2:8888/MAMP/hotel/ViewReviews.php");
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ViewReviews.this);
            pd.setMessage("Searching for reviews...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                try {
                    URL url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
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
                if(result.contentEquals("nothing"))
                {
                    LinearLayout layout = findViewById(R.id.lv5);
                    TextView tv;
                    tv= new TextView(ViewReviews.this);
                    tv.append("No reviews made yet");
                    layout.addView(tv);

                }else
                {
                    final ArrayList<ReviewData> p = proccessData(result);
                    if(p != null)
                    {
                        LinearLayout layout = findViewById(R.id.lv5);
                        TextView tv;
                        for(final ReviewData elem : p)
                        {
                            tv= new TextView(ViewReviews.this);
                            tv.append("Guest comment: " +elem.comment +"\n \n");
                            tv.append("Review left by: " + elem.fname + " " + elem.lname + "\n");
                            tv.append("Date: "+elem.date + "\n");
                            tv.append("------------------------------------------------------------------------------------------------------------");
                            layout.addView(tv);
                        }
                    }
                }

            }catch (Exception e)
            {
                Toast.makeText(ViewReviews.this,"Something went wrong on our side, please try again later",Toast.LENGTH_LONG).show();
            }
        }

    }

    private ArrayList<ReviewData> proccessData(String data)
    {
        ArrayList<ReviewData> temp = new ArrayList<>();
        try{
            JSONArray ar = new JSONArray(data);
            JSONObject element;
            ReviewData rd;
            for(int i=0; i<ar.length();i++)
            {
                element = ar.getJSONObject(i);
                rd = new ReviewData();
                rd.fname = element.getString("Fname");
                rd.lname = element.getString("Lname");
                rd.comment = element.getString("Comment");
                rd.date = element.getString("Date");
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
