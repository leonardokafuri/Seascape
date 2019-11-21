package com.example.seascapehotel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FeedBack extends AppCompatActivity {
    ProgressDialog pd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Button sendReview = findViewById(R.id.sendreview);
        final TextView comments = findViewById(R.id.review);
        final SharedPreferences preferences = this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);
        sendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               int i= preferences.getInt("CID",0);
                if(i==0)
                {
                    Toast.makeText(FeedBack.this,"You need to Login first to make a review",Toast.LENGTH_LONG).show();
                }else{
                    String comment = comments.getText().toString();
                    preferences.edit().putString("review", comment).apply();
                    new JsonTask().execute("http://10.0.2.2:8888/MAMP/hotel/Review.php");
                }

            }
        });

    }

    private class JsonTask extends AsyncTask<String, String, String> {
        final SharedPreferences preferences =FeedBack.this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(FeedBack.this);
            pd.setMessage("Saving your feedback ...");
            pd.setCancelable(false);
            pd.show();
        }


        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            Calendar c = Calendar.getInstance();
            String fname = preferences.getString("lgfname","");
            String lname = preferences.getString("lglname","");
            String comment = preferences.getString("review","");
            String date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());

            try {
                String data = URLEncoder.encode("review","UTF-8")+ "=" +URLEncoder.encode(comment,"UTF-8");
                data += "&" + URLEncoder.encode("date","UTF-8")+ "="+ URLEncoder.encode(date,"UTF-8");
                data += "&" + URLEncoder.encode("fname","UTF-8")+ "="+ URLEncoder.encode(fname,"UTF-8");
                data += "&" + URLEncoder.encode("lname","UTF-8")+ "="+ URLEncoder.encode(lname,"UTF-8");
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
                if (result.contentEquals("error"))
                {
                    Toast.makeText(FeedBack.this,"Something went wrong trying to add your feedback,please try again...",Toast.LENGTH_LONG).show();
                }else
                    startActivity(new Intent(FeedBack.this,MainActivity.class));

            }catch (Exception e){
                Toast.makeText(FeedBack.this,"Something went wrong on our side, please try again later",Toast.LENGTH_LONG).show();
            }

        }

    }
}
