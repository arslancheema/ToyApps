package com.example.aarshad.toyapps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemorablePlaces_MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MemorablePlaces_MapsActivity.class.getSimpleName();
    private GoogleMap mMap;

    LocationManager locationManager ;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorable_places__maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        if (intent.getIntExtra("placenumber", 0)==0){
            // Zoom in on User's location

            locationManager  = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centerOnUserLocation(location, "Your Location");
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

            if (Build.VERSION.SDK_INT <= 23 ){
                Log.i(TAG, "Less than 23");
                // start requesting location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);

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
                       centerOnUserLocation(location,"Your Location");
                    }
                }

            }

        } else{
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MemorablePlaces_MainActivity.locations.get(intent.getIntExtra("placenumber",0)).latitude);
            placeLocation.setLongitude(MemorablePlaces_MainActivity.locations.get(intent.getIntExtra("placenumber",0)).longitude);
            centerOnUserLocation(placeLocation,MemorablePlaces_MainActivity.places.get(intent.getIntExtra("placenumber",0)));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // we have permission but we are again confirming
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "Given Permission");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerOnUserLocation(lastKnownLocation,"Your Location");

            }
        }
    }

    public void centerOnUserLocation (Location location, String title){

        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.clear();

        if (title!="Your Location"){
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,14));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                String address = "";
                try {
                    List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,2);

                    if (listAddresses != null && listAddresses.size()>0){

                        if (listAddresses.get(1).getThoroughfare() !=null){

                            if (listAddresses.get(1).getSubThoroughfare() !=null) {
                                address += listAddresses.get(1).getSubThoroughfare() + " " ;
                            }
                            address += listAddresses.get(1).getThoroughfare() + " " ;

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (address==""){
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    address = currentDateTimeString;
                }

                mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

                MemorablePlaces_MainActivity.places.add(address);
                MemorablePlaces_MainActivity.locations.add(latLng);

                MemorablePlaces_MainActivity.arrayAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "Location Saved", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
