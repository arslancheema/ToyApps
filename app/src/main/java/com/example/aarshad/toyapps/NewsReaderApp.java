package com.example.aarshad.toyapps;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NewsReaderApp extends AppCompatActivity {

    private static final String TAG = NewsReaderApp.class.getSimpleName() ;

    ArrayList<String> titlesList = new ArrayList<>();

    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader_app);

        ListView newsListView = (ListView) findViewById(R.id.news_reader_listview);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,titlesList);

        newsListView.setAdapter(arrayAdapter);

        DownloadTask downloadTask = new DownloadTask();
        try{
            downloadTask.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        } catch (Exception e ){
            e.printStackTrace();
        }

    }

    public class DownloadTask extends AsyncTask<String,Void,String>{



        @Override
        protected String doInBackground(String... strings) {

            String result = "";

            URL url ;

            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data!=-1){
                char current = (char) data ;
                result+=current;
                data = reader.read();
                }

                Log.v(TAG,"Result : " + result);

                JSONArray jsonArray = new JSONArray(result);

                int numOfItems = 20;

                if (jsonArray.length()<20){
                    numOfItems = jsonArray.length();
                }

                for (int i=0;i< numOfItems;i++){
                    String newsID = jsonArray.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + newsID + ".json?print=pretty");
                    urlConnection = (HttpURLConnection) url.openConnection();

                    in = urlConnection.getInputStream();
                    reader = new InputStreamReader(in);

                    data = reader.read();

                    String newsDetails = "";

                    while (data!=-1){
                        char current = (char) data ;
                        newsDetails+=current;
                        data = reader.read();
                    }

                    JSONObject jsonObject = new JSONObject(newsDetails);

                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url") ){

                        String newsTitle = jsonObject.getString("title");
                        String newsUrl = jsonObject.getString("url");

                        Log.v(TAG, "Title: " + newsTitle + "  URL: " + newsUrl );
                    }

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
