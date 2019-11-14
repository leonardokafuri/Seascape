package com.example.seascapehotel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Register extends AppCompatActivity {
    EditText  mEmail,mPassword, mFirstName,mLastName;
    Button mRegister;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegister = findViewById(R.id.register);
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        mFirstName = findViewById(R.id.firstName);
        mLastName = findViewById(R.id.lastName);
        mRegister = findViewById(R.id.register);
        Button regbtn = findViewById(R.id.register);

        final SharedPreferences preferences = this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Authentication
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if(!email.matches(emailPattern)){
                    mEmail.setError("Invalid Email");
                   mEmail.requestFocus();
                    return;
                }

               if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required");
                    mPassword.requestFocus();
                    return;
                }
               if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required");
                    mEmail.requestFocus();
                    return;
                }

               if(password.length() < 5){
                    mPassword.setError("Password must be bigger or equals than 5 Characters");
                    mPassword.requestFocus();
                    return;
                }
              if(password.length() >= 25){
                    mPassword.setError("Password must be lesser than 25 Characters");
                    mPassword.requestFocus();
                    return;
                }
                else{
                  if(mEmail.getText().toString().isEmpty() || mPassword.getText().toString().isEmpty() || mFirstName.getText().toString().isEmpty()|| mLastName.getText().toString().isEmpty())
                  {
                      Toast.makeText(Register.this, "Please fill the code and the new password fields", Toast.LENGTH_SHORT).show();
                  }
                  else{

                  }

                    String remail = mEmail.getText().toString();
                    String rpass = mPassword.getText().toString();
                    String rfname = mFirstName.getText().toString();
                    String rlname = mLastName.getText().toString();
                  preferences.edit().putString("remail",remail).apply();
                  preferences.edit().putString("rpass",rpass).apply();
                  preferences.edit().putString("rfname",rfname).apply();
                  preferences.edit().putString("rlname",rlname).apply();
                  //new JsonTask().execute("http://10.0.2.2:8888/MAMP/hotel/Register.php");
                  new JsonTask().execute("http://ec2-3-83-207-177.compute-1.amazonaws.com/Register.php");
                }
            }
        });
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        final SharedPreferences preferences =Register.this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Register.this);
            pd.setMessage("Registring you ...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String email = preferences.getString("remail","");
            String fname = preferences.getString("rfname","");
            String lname = preferences.getString("rlname","");
            String pass = preferences.getString("rpass","");
            try {
                String data = URLEncoder.encode("email","UTF-8")+ "=" +URLEncoder.encode(email,"UTF-8");
                data += "&" + URLEncoder.encode("password","UTF-8")+ "="+ URLEncoder.encode(pass,"UTF-8");
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
               if(result.contentEquals("Emailerr")) // checking string results to give error messages not working for some reason
               {
                   Toast.makeText(Register.this,"This email is already registered, try a different one",Toast.LENGTH_SHORT).show();
               }else if(result.equals("error"))
               {
                   Toast.makeText(Register.this,"We could not register you, please try again",Toast.LENGTH_SHORT).show();
               }
               else
               {
                   startActivity(new Intent(Register.this,SignIn.class)); // registring new users done !
               }

        }

    }
}
