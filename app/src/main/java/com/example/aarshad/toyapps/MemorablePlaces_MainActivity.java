package com.example.aarshad.toyapps;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MemorablePlaces_MainActivity extends AppCompatActivity {

    public static ArrayList<LatLng> locations  = new ArrayList<>();
    public static ArrayList<String> places = new ArrayList<>();
    public static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorable_places__main);

        ListView listView = (ListView) findViewById(R.id.listview_memorable_places);

        places.add("Add a new place");

        locations.add(new LatLng(0,0));

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent i = new Intent(getApplicationContext(),MemorablePlaces_MapsActivity.class);
                i.putExtra("placenumber",position);
                startActivity(i);
            }
        });


    }
}
