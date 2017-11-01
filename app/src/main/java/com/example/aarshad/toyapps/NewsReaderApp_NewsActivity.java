package com.example.aarshad.toyapps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsReaderApp_NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader_app__news);

        WebView webView = (WebView) findViewById(R.id.news_reader_webview);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        Intent i = getIntent();

        webView.loadData(i.getStringExtra("content"),"text/html","UTF-8");

    }
}
