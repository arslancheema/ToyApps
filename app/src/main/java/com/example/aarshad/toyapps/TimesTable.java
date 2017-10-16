package com.example.aarshad.toyapps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;

public class TimesTable extends AppCompatActivity {

    SeekBar tablesSeekbar;
    ListView listView ;

    ArrayList<String> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.times_table_main_layout);
        tablesSeekbar  = (SeekBar)findViewById(R.id.seekBar);
        listView = (ListView) findViewById(R.id.listview);

        tablesSeekbar.setMax(20);

        tablesSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                int min = 1 ;
                if (progress < min){
                    progress = min ;
                    tablesSeekbar.setProgress(progress);
                }

                Log.v("MainActivity", String.valueOf(progress));
                setUpArrayAdapter(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void setUpArrayAdapter (int tableValue){
        arrayList.clear();
        ArrayAdapter<String> arrayAdapter ;
        for (int i=1 ; i < 10 ; i++){
            arrayList.add(String.valueOf(tableValue*i));
        }
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
    }
}
