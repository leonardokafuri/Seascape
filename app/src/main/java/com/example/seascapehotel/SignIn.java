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

public class SignIn extends AppCompatActivity {
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        Button forget = findViewById(R.id.ForgotPass);
        Button signin = findViewById(R.id.signIn);
        final SharedPreferences preferences = this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this,Password.class ));
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memail = email.getText().toString();
                String mpass = password.getText().toString();
                preferences.edit().putString("memail",memail).apply();
                preferences.edit().putString("mpass",mpass).apply();
                new JsonTask().execute("http://ec2-54-196-138-183.compute-1.amazonaws.com/hotel/Login.php");
               // new JsonTask().execute("http://ec2-3-83-207-177.compute-1.amazonaws.com/Login.php");
            }
        });
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        final SharedPreferences preferences =SignIn.this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SignIn.this);
            pd.setMessage("Login you in ...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String dbemail = preferences.getString("memail", "");
            String dbpass = preferences.getString("mpass", "");
            try {
                String data = URLEncoder.encode("Email","UTF-8")+ "=" +URLEncoder.encode(dbemail,"UTF-8");
                data += "&" + URLEncoder.encode("Password","UTF-8")+ "="+ URLEncoder.encode(dbpass,"UTF-8");
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
                if (result.contentEquals("nothing")) {
                    Toast.makeText(SignIn.this, "Your email or password is wrong, please try again", Toast.LENGTH_LONG).show();
                } else {
                    String dbemail = preferences.getString("memail", "");
                    String dbpass = preferences.getString("mpass", "");
                    final ArrayList<LoginData> p = proccessData(result);
                    if (p != null) {
                        for (LoginData elem : p) {
                            int id = elem.CustomerID;
                            String lname = elem.Lastname;
                            String fname = elem.Firstname;
                            preferences.edit().putInt("CID", id).apply();
                            preferences.edit().putString("lgfname",fname).apply();
                            preferences.edit().putString("lglname",lname).apply();
                            if (elem.Email.contentEquals(dbemail) && elem.Password.contentEquals(dbpass))
                                startActivity(new Intent(SignIn.this, MainActivity.class));
                        }

                    }

                }
            }catch (Exception e)
            {
                Toast.makeText(SignIn.this,"Something went wrong on our side, please try again later",Toast.LENGTH_LONG).show();
            }
        }

    }
    private ArrayList<LoginData> proccessData(String data)
    {
        ArrayList<LoginData> temp = new ArrayList<>();
        try{
            JSONArray ar = new JSONArray(data);
            JSONObject element;
            LoginData ld;
            for(int i=0; i<ar.length();i++)
            {
                element = ar.getJSONObject(i);
                ld = new LoginData();
                ld.Email = element.getString("Email");
                ld.Password = element.getString("Password");
                ld.CustomerID = element.getInt("CustomerID");
                ld.Firstname = element.getString("FirstName");
                ld.Lastname = element.getString("LastName");
                temp.add(ld);
            }
            return  temp;

        }catch (JSONException E)
        {
            Log.d("MainActivity",E.getMessage());
        }
        return null;
    }
}
