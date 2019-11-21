package com.example.seascapehotel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Payment extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        final String expiryDatePattern = "(?:0[1-9]|1[0-2])/[0-9]{2}";
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
                  if (cardholder.getText().toString().isEmpty() && cardnumber.getText().toString().isEmpty() && postal.getText().toString().isEmpty() && expdate.getText().toString().isEmpty()
                          && cvv.getText().toString().isEmpty() && code.getText().toString().isEmpty() && phone.getText().toString().isEmpty()) {
                Toast.makeText(Payment.this, "Please fill the form fields", Toast.LENGTH_SHORT).show();}
                else {
                          if(TextUtils.isEmpty(cardholder.getText().toString())){
                              cardholder.setError("Card Name is Required");
                              cardholder.requestFocus();
                              return;
                          }
                          if(TextUtils.isEmpty(cardnumber.getText().toString())){
                              cardnumber.setError("Card Number is Required");
                              cardnumber.requestFocus();
                              return;
                          }
                          if(TextUtils.isEmpty(postal.getText().toString())){
                              postal.setError("Postal Code is Required");
                              postal.requestFocus();
                              return;
                          }

                          if(TextUtils.isEmpty(expdate.getText().toString())){
                              expdate.setError("Expiry Date is Required");
                              expdate.requestFocus();
                              return;
                          }
                          else if(!expdate.getText().toString().matches(expiryDatePattern)){
                              expdate.setError("Invalid Expiry Date");
                              expdate.requestFocus();
                              return;
                          }

                          if(TextUtils.isEmpty(cvv.getText().toString())){
                              cvv.setError("CVV is Required");
                              cvv.requestFocus();
                              return;
                          }
                          if((Integer.parseInt(cvv.getText().toString()) <= 2)){
                              cvv.setError("CVV Must Have 3 Numbers");
                              cvv.requestFocus();
                              return;
                          }

                          if(TextUtils.isEmpty(code.getText().toString())){
                              code.setError("Country Code is Required");
                              code.requestFocus();
                              return;
                          }
                          if(TextUtils.isEmpty(phone.getText().toString())){
                              phone.setError("Mobile Phone is Required");
                              phone.requestFocus();
                              return;
                          }
                          if(cardnumber.getText().toString().length() >= 16){
                              cardnumber.setError("Card Number must be lesser than 16 Characters");
                              cardnumber.requestFocus();
                              return;
                          }
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




            }
        });

    }
}

