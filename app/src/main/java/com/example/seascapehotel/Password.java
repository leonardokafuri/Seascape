package com.example.seascapehotel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Password extends AppCompatActivity {
    ProgressDialog pd;
    DatabaseHelper dbh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        final EditText email = findViewById(R.id.Resetmail);
        final EditText sendCode = findViewById(R.id.ResetCode);
        final EditText newPass = findViewById(R.id.newpass);
        final Button btSendCode = findViewById(R.id.SendCode);
        final Button btReset = findViewById(R.id.Reset);
        dbh = new DatabaseHelper(this);
        final SharedPreferences preferences = this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);

        btSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String useremail = email.getText().toString();
                    preferences.edit().putString("rpassemail",useremail).apply();
                    new JsonTask().execute("http://10.0.2.2:8888/MAMP/hotel/ForgotPassword.php");
                    String dbmail = preferences.getString("resetmail", "");
                    int cid = preferences.getInt("rcid",0);
                    String token = dbh.createResetToken(cid);
                    SendMailTLS.sendEmail(dbmail,token);
                    Toast.makeText(Password.this, "An email has been sent to you", Toast.LENGTH_SHORT).show();
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        });
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = newPass.getText().toString();
                preferences.edit().putString("newpass",password).apply();

            }
        });

    }

    private class JsonTask extends AsyncTask<String, String, String> {
        final SharedPreferences preferences =Password.this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Password.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String rsetemail = preferences.getString("rpassemail", "");
            try {
                String data = URLEncoder.encode("Email","UTF-8")+ "=" +URLEncoder.encode(rsetemail,"UTF-8");
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
            String usermail = preferences.getString("rpassemail", "");
            final ArrayList<PasswordData> p = proccessData(result);
                if(p != null)
                {
                    for(PasswordData elem : p)
                    {
                        if(elem.Email.contentEquals(usermail))
                        {
                                 preferences.edit().putString("resetmail",usermail).apply();
                                 preferences.edit().putInt("rcid",elem.CustomerID).apply();
                        }
                        else
                        {
                            Toast.makeText(Password.this, "Email not Registered", Toast.LENGTH_SHORT).show();
                        }

                    }
            }
        }
    }

    private class JsonTask2 extends AsyncTask<String, String, String> {
        final SharedPreferences preferences =Password.this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Password.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String rsetemail = preferences.getString("rpassemail", "");
            try {
                String data = URLEncoder.encode("Email","UTF-8")+ "=" +URLEncoder.encode(rsetemail,"UTF-8");
                //data += "&" + URLEncoder.encode("Password","UTF-8")+ "="+ URLEncoder.encode(pass,"UTF-8");
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
            String usermail = preferences.getString("rpassemail", "");
            final ArrayList<PasswordData> p = proccessData(result);
            if(p != null)
            {
                for(PasswordData elem : p)
                {
                    if(elem.Email.contentEquals(usermail))
                    {
                        preferences.edit().putString("resetmail",usermail).apply();
                        preferences.edit().putInt("rcid",elem.CustomerID).apply();
                    }
                    else
                    {
                        Toast.makeText(Password.this, "Email not Registered", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    }

    private ArrayList<PasswordData> proccessData(String data)
    {
        ArrayList<PasswordData> temp = new ArrayList<>();
        try{
            JSONArray ar = new JSONArray(data);
            JSONObject element;
            PasswordData passr;
            for(int i=0; i<ar.length();i++)
            {
                element = ar.getJSONObject(i);
                passr = new PasswordData();
                passr.Email = element.getString("Email");
                temp.add(passr);
            }
            return  temp;

        }catch (JSONException E)
        {
            Log.d("MainActivity",E.getMessage());
        }
        return null;
    }




}
