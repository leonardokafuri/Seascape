package com.example.seascapehotel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker at Douglas College and move the camera
        //mMap.getCameraPosition();

        //49.203575, -122.912765

        LatLng Seascape = new LatLng(49.203575, -122.912765);
        LatLng MyLocation = new LatLng(49.203707, -122.911805);
        LatLng DouglasCollege = new LatLng(49.203566, -122.912712);
        LatLng TimHortons = new LatLng(49.201552, -122.913026);
        LatLng oldSpagetti = new LatLng(49.202124, -122.912228);
        LatLng cinema = new LatLng(49.200813, -122.913215);
        LatLng oldBavaria = new LatLng(49.208870, -122.914168);
        LatLng hyack = new LatLng(49.202568, -122.912602);
        LatLng banh = new LatLng(49.203000, -122.910778);
        LatLng pizza = new LatLng(49.204360, -122.909190);
        LatLng bubble = new LatLng(49.204970, -122.910177);

        //49.203002, -122.910789
        String message = "Show your active QR code and get ";

        //mMap.addMarker(new MarkerOptions().position(DouglasCollege).title("Marker in Douglas College").snippet("Your location"));
        mMap.addMarker(new MarkerOptions().position(Seascape).title("Seascape Hotel").snippet("Your hotel is here!").icon(BitmapDescriptorFactory.fromResource(R.drawable.hotel)));
        mMap.addMarker(new MarkerOptions().position(TimHortons).title("Tim Hortons").snippet(message+"25% discount on all beverages!").icon(BitmapDescriptorFactory.fromResource(R.drawable.percentage)));
        mMap.addMarker(new MarkerOptions().position(oldSpagetti).title("The Old Spaghetti").snippet(message+"15% discount and 1 free drink!").icon(BitmapDescriptorFactory.fromResource(R.drawable.percentage)));
        mMap.addMarker(new MarkerOptions().position(cinema).title("LandMark Cinema").snippet(message+"20% discount on tickets!").icon(BitmapDescriptorFactory.fromResource(R.drawable.percentage)));
        mMap.addMarker(new MarkerOptions().position(oldBavaria).title("The Old Bavaria Haus Restaurant").snippet(message+"10% discount!").icon(BitmapDescriptorFactory.fromResource(R.drawable.percentage)));
        mMap.addMarker(new MarkerOptions().position(hyack).title("Hyack Sushi").snippet(message+"10% discount!").icon(BitmapDescriptorFactory.fromResource(R.drawable.percentage)));
        mMap.addMarker(new MarkerOptions().position(banh).title("Banh Mi Bar").snippet(message+"10% discount!").icon(BitmapDescriptorFactory.fromResource(R.drawable.percentage)));
        mMap.addMarker(new MarkerOptions().position(bubble).title("Bubble World").snippet(message+"10% discount!").icon(BitmapDescriptorFactory.fromResource(R.drawable.percentage)));
        mMap.addMarker(new MarkerOptions().position(pizza).title("Pizzeria Ludica").snippet(message+"10% discount!").icon(BitmapDescriptorFactory.fromResource(R.drawable.percentage)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Seascape, 16));




        getLocationPermission();
    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }else{
            //Set location enable
            mMap.setMyLocationEnabled(true);

            //Set Location button enable
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }


}
