package com.example.aarshad.toyapps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HikersLocationLog extends AppCompatActivity {

    LocationManager locationManager ;

    LocationListener locationListener ;

    public static final String TAG = HikersLocationLog.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hikers_location_log);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        Log.i(TAG, "OnCreate");

        if (Build.VERSION.SDK_INT <= 23 ){
            Log.i(TAG, "Less than 23");
            // start requesting location updates
            startListening();

        } else {
            // check if we have the permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // if not granted then ask for Permission
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            } else {
                // If there is already the permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location!=null){
                    updateLocationInfo(location);
                }
            }

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // we have permission but we are again confirming
            startListening();
        }
    }

    public void updateLocationInfo(Location location){
        Log.i(TAG, "New Location: " + location.toString());

        Toast.makeText(this, "Location Changed" , Toast.LENGTH_SHORT).show();
        TextView latTxtView = (TextView) findViewById(R.id.latitude);
        TextView altTxtView = (TextView) findViewById(R.id.altitude);
        TextView lngTxtView = (TextView) findViewById(R.id.longitude);
        TextView accTxtView = (TextView) findViewById(R.id.accuracy);
        TextView addTxtView = (TextView) findViewById(R.id.address);

        latTxtView.setText("Latitude: "  + location.getLatitude());
        altTxtView.setText("Altitude: "  + location.getAltitude());
        lngTxtView.setText("Longitude: "  + location.getLongitude());
        accTxtView.setText("Accuracy: "  + location.getAccuracy());

        // To get the address
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            // 1 is the number of address we want to get
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),2);

            Log.i(TAG, String.valueOf(listAddresses.size()));

            String address = "Couldn't find the address";
            if (listAddresses != null && listAddresses.size()>0){
                Log.i(TAG, listAddresses.get(1).toString());

                address = "";
                if (listAddresses.get(1).getSubThoroughfare() !=null){
                    address+=listAddresses.get(1).getSubThoroughfare() + "\n";
                }
                if (listAddresses.get(1).getThoroughfare() !=null){
                    address+=listAddresses.get(1).getThoroughfare() + "\n";
                }
                if (listAddresses.get(1).getLocality() !=null){
                    address+=listAddresses.get(1).getLocality() + "\n";
                }
                if (listAddresses.get(1).getPostalCode() !=null){
                    address+=listAddresses.get(1).getPostalCode() + "\n";
                }
                if (listAddresses.get(1).getCountryName() !=null){
                    address+=listAddresses.get(1).getCountryName() + "\n";
                }

                addTxtView.setText(address);

            }

        } catch (IOException e) {
            Log.e(TAG,"Exception is Geocoder");
            e.printStackTrace();
        }


    }

    public void startListening (){
        Log.i(TAG, "StartListening");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "have Permission");
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        }
    }
}
