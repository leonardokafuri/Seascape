package com.example.seascapehotel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Payment extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        final EditText code = findViewById(R.id.codeEditText);
        final EditText phone = findViewById(R.id.mobileEditText);
        final EditText cardholder = findViewById(R.id.nameEditText);
        final EditText cardnumber = findViewById(R.id.cardnumberEditText);
        final EditText expdate = findViewById(R.id.expdateEditText);
        final EditText cvv = findViewById(R.id.cvvEditText);
        final EditText postal = findViewById(R.id.postalcodeEditText);
        Button payBtn = findViewById(R.id.payButton);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Payment.this);
                builder.setTitle("Confirm before purchase")
                        .setMessage("Card Name: " + cardholder.getText().toString()  +
                                "\n" + "Card Number: "+cardnumber.getText().toString() + "\n" +
                                "Expiration Date: "+expdate.getText().toString() + "\n" +
                                "CVV: "+cvv.getText().toString() + "\n" +
                                "Postal Code: "+postal.getText().toString() + "\n" +
                                "Mobile Number: "+ code.getText().toString() + " "+phone.getText().toString() + "\n")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Payment.this,SuccessfulPayment.class));
                            }
                        })
                        .setNegativeButton("CANCEL",null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(Payment.this);
        builder.setTitle("Really Exit?")
                .setMessage("Are you sure?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Payment.super.onBackPressed();
                    }
                })
                .setNegativeButton("CANCEL",null);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
