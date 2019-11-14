package com.example.seascapehotel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register extends AppCompatActivity {
    EditText  mEmail,mPassword, mFirstName,mLastName;
    Button mRegister;

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
                    //add user to dabatase

                    //then go to new activity
                    startActivity(new Intent(Register.this,SignIn.class));
                }


            }
        });
    }
}
