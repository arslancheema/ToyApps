package com.example.aarshad.toyapps;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    ArrayList<String> contentsList = new ArrayList<>();

    ArrayAdapter arrayAdapter;

    SQLiteDatabase newsDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader_app);

        ListView newsListView = (ListView) findViewById(R.id.news_reader_listview);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,titlesList);

        newsListView.setAdapter(arrayAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(),NewsReaderApp_NewsActivity.class);
                intent.putExtra("content",contentsList.get(position));
                startActivity(intent);
            }
        });

        newsDB = this.openOrCreateDatabase("News",MODE_PRIVATE,null);

        newsDB.execSQL("CREATE TABLE IF NOT EXISTS news (id INTEGER PRIMARY KEY , newsId INTEGER, title VARCHAR , contents VARCHAR)");

        updateListView();


        DownloadTask downloadTask = new DownloadTask();
        try{
            //downloadTask.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        } catch (Exception e ){
            e.printStackTrace();
        }

    }

    public void updateListView (){
        Cursor c = newsDB.rawQuery("SELECT * FROM news",null);
        int contentIndex = c.getColumnIndex("contents");
        int titleIndex = c.getColumnIndex("title");

        if (c.moveToFirst()){
            titlesList.clear();
            contentsList.clear();

            do {
                titlesList.add(c.getString(titleIndex));
                contentsList.add(c.getString(contentIndex));
            } while (c.moveToNext());
        }

        arrayAdapter.notifyDataSetChanged();

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

                Log.v(TAG,"All News Ids : " + result);

                JSONArray jsonArray = new JSONArray(result);

                int numOfItems = 10;

                if (jsonArray.length()<numOfItems){
                    numOfItems = jsonArray.length();
                }

                newsDB.execSQL("DELETE FROM news");

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
                    Log.v(TAG, "News Overview: " + newsDetails);



                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url") ){

                        String newsTitle = jsonObject.getString("title");
                        String newsUrl = jsonObject.getString("url");

                        Log.v(TAG,"ID: "+ jsonObject.getString("id") + " , Title: " + newsTitle + " , URL: " + newsUrl );

                        url = new URL(newsUrl);
                        urlConnection = (HttpURLConnection) url.openConnection();

                        in = urlConnection.getInputStream();
                        reader = new InputStreamReader(in);

                        data = reader.read();

                        String newsContents = "";

                        while (data!=-1){
                            char current = (char) data ;
                            newsContents+=current;
                            data = reader.read();
                        }

                        String sql = "INSERT INTO news (newsId,title,contents) VALUES (? , ? , ?)";
                        SQLiteStatement sqLiteStatement = newsDB.compileStatement(sql);
                        sqLiteStatement.bindString(1,newsID);
                        sqLiteStatement.bindString(2,newsTitle);
                        sqLiteStatement.bindString(3,newsContents);

                        sqLiteStatement.execute();

                        Log.v(TAG, "Inserted: newsID : " + newsID + " Title: " + newsTitle );

                    }

                }


            } catch (MalformedURLException e) {
                Log.e(TAG,"MalformedURLException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG,"IOException");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(TAG,"JSONException");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v(TAG, "onPostExecute");
            updateListView();
        }
    }
}
