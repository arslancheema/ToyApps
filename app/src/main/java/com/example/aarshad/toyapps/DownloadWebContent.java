package com.example.aarshad.toyapps;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class DownloadWebContent extends AppCompatActivity {

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection urlConnection = null ;
            String result = "ab";

            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                // hold the data that comes in
                InputStream in = urlConnection.getInputStream();
                // read the data
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                // last one would have value of -1
                while (data!=-1){

                    char current = (char)data ;
                    result+= current;

                    data = reader.read();
                }

                return result ;


            } catch (Exception e ){
               Log.e("Exception","exception", e);
                return "Exception";
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_web_content);

        DownloadTask task = new DownloadTask();
        String result = "";

        try {
            result = task.execute("https://www.ecowebhosting.co.uk/").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.i("Result", result);

    }
}
