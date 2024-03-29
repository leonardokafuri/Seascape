package com.example.seascapehotel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RoomKey extends AppCompatActivity {

    ProgressDialog pd;
    String URL = API.URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_key);

            new JsonTask().execute(URL+"GenerateQR.php");
    }
    private int getDimension() {
        //Find screen size
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3/4;
        return smallerDimension;
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        final SharedPreferences preferences =RoomKey.this.getSharedPreferences(
                "mypref", Context.MODE_PRIVATE);

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(RoomKey.this);
            pd.setMessage("Generating Key...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            int id = preferences.getInt("CID",0);
            String userid = String.valueOf(id);
            String checkin = preferences.getString("bkci","");
            String checkout = preferences.getString("bkco","");
            try {
                String data = URLEncoder.encode("CustomerID","UTF-8")+ "=" +URLEncoder.encode(userid,"UTF-8");
                data += "&" + URLEncoder.encode("Checkout","UTF-8")+ "="+ URLEncoder.encode(checkout,"UTF-8");
                data += "&" + URLEncoder.encode("Checkin","UTF-8")+ "="+ URLEncoder.encode(checkin,"UTF-8");
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
                        Log.d("QR: ", "> " + line);   //here u ll get whole response...... :-)
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
                Calendar c = Calendar.getInstance();
                final ImageView iv = (ImageView) findViewById(R.id.imgV);
                final TextView tv = (TextView) findViewById(R.id.txtView);
                if(result.contentEquals("error"))
                {
                    tv.append("Something went wrong :(");
                }else
                {
                    final ArrayList<QRcodeData> p = proccessData(result);
                    if(p != null)
                    {
                        for(final QRcodeData elem : p)
                        {
                            if(c.getTime().before(new SimpleDateFormat("yyyy-MM-dd").parse(elem.Checkin)) || c.getTime().after(new SimpleDateFormat("yyyy-MM-dd").parse(elem.Checkout)))
                            {
                                    String invalid ="NOT ACTIVATED";
                                    String mesg = elem.ReservationID;
                                    mesg += ","+elem.CustomerID+",";
                                    mesg+= elem.Checkin+",";
                                    mesg += elem.Checkout+ ",";
                                    mesg += elem.RoomID +",";
                                    mesg += invalid;
                                    int dimension =getDimension();
                                    Bitmap bitmap = QRCode.generate(mesg, dimension, RoomKey.this);
                                    tv.append("Your room is : " + elem  .RoomID +"\n");
                                    tv.append("Your key status is : " + invalid);
                                    iv.setImageBitmap(bitmap);

                            }
                            else
                            {
                                String valid ="ACTIVATED";
                                String mesg = elem.ReservationID;
                                mesg += ","+elem.CustomerID+",";
                                mesg+= elem.Checkin+",";
                                mesg += elem.Checkout+ ",";
                                mesg += elem.RoomID +",";
                                mesg += valid;
                                int dimension =getDimension();
                                Bitmap bitmap = QRCode.generate(mesg, dimension, RoomKey.this);
                                tv.append("Your room is : " + elem  .RoomID +"\n");
                                tv.append("Your key status is : " + valid);
                                iv.setImageBitmap(bitmap);
                            }
                        }
                    }
                }

            }catch (Exception e)
            {
                final ImageView iv = (ImageView) findViewById(R.id.imgV);
                final TextView tv = (TextView) findViewById(R.id.txtView);
                Calendar c = Calendar.getInstance();

                try {
                    if(c.getTime().before(new SimpleDateFormat("yyyy-MM-dd").parse( preferences.getString("bkci",""))) || c.getTime().after(new SimpleDateFormat("yyyy-MM-dd").parse( preferences.getString("bkco",""))))
                    {
                        String invalid ="NOT ACTIVATED";
                        String mesg = preferences.getString("qrRoom","");
                        mesg += ","+preferences.getInt("CID",0)+",";
                        mesg+= preferences.getString("qrRoom","")+",";
                        mesg+= preferences.getString("bkci","")+",";
                        mesg+= preferences.getString("bkco","");
                        mesg += invalid;
                        int dimension =getDimension();
                        Bitmap bitmap = QRCode.generate(mesg, dimension, RoomKey.this);
                        tv.append("Your room is : " + preferences.getString("qrRoom","")+"\n");
                        tv.append("Your key status is : " + invalid);
                        iv.setImageBitmap(bitmap);
                    }
                    else
                    {

                        String valid ="ACTIVATED";
                        String mesg = preferences.getString("qrRoom","");
                        mesg += ","+preferences.getInt("CID",0)+",";
                        mesg+= preferences.getString("qrRoom","")+",";
                        mesg+= preferences.getString("bkci","")+",";
                        mesg+= preferences.getString("bkco","");
                        mesg += valid;
                        int dimension =getDimension();
                        Bitmap bitmap = QRCode.generate(mesg, dimension, RoomKey.this);
                        tv.append("Your room is : " + preferences.getString("qrRoom","")+"\n");
                        tv.append("Your key status is : " + valid);
                        iv.setImageBitmap(bitmap);
                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                Toast.makeText(RoomKey.this,"Check your internet connection",Toast.LENGTH_LONG).show();
            }
        }

    }
    private ArrayList<QRcodeData> proccessData(String data)
    {
        ArrayList<QRcodeData> temp = new ArrayList<>();
        try{
            JSONArray ar = new JSONArray(data);
            JSONObject element;
            QRcodeData qr;
            for(int i=0; i<ar.length();i++)
            {
                element = ar.getJSONObject(i);
                qr = new QRcodeData();
                qr.ReservationID = element.getString("ReservationID");
                qr.CustomerID = element.getString("CustomerID");
                qr.Checkin = element.getString("checkin");
                qr.Checkout = element.getString("checkout");
                qr.RoomID = element.getString("RoomID");
                temp.add(qr);
            }
            return temp;

        }catch (JSONException E)
        {
            Log.d("MainActivity",E.getMessage());
        }
        return null;
    }
}
