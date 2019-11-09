package com.example.seascapehotel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

public class Password extends AppCompatActivity {

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

        btSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String useremail = email.getText().toString();
                    // perform a query to check if user email is on the database and store his userid,if so send him an email with the code
                    /*
                    if(c.getCount() == 0)
                        Toast.makeText(ForgotPassword.this, "This email is not registered in our systems, Try a new email and hit send code again!", Toast.LENGTH_LONG).show();
                    else{
                        c.moveToFirst();
                        String token = dbh.createResetToken(c.getInt(0));
                        if(token.isEmpty())
                        {
                            Toast.makeText(ForgotPassword.this, "Something went wrong when trying to send the code, please check your info and try again", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            SendMailTLS.sendEmail(email.getText().toString(),token);
                            Toast.makeText(ForgotPassword.this, "An email has been sent to your address, please check your token and fill it on the code field along with your new password", Toast.LENGTH_SHORT).show();
                        }
                    }*/

                    SendMailTLS.sendEmail(email.getText().toString(),"testing");

                }
                catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        });


        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sendCode.getText().toString().isEmpty() || newPass.getText().toString().isEmpty())
                {
                    Toast.makeText(Password.this, "Please fill the code and the new password fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                        dbh.resetPassword(email.getText().toString(),sendCode.getText().toString(),newPass.getText().toString());
                        Toast.makeText(Password.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Password.this,Login.class));
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(Password.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

}
