package edu.uiuc.cs427app;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // grabbing the strings from the intent
        String cityName = getIntent().getStringExtra("cityName");
        String lat = getIntent().getStringExtra("lat");
        String lon = getIntent().getStringExtra("lon");
        // String testurl = "google.com";

        System.out.println("map view for lat: " + lat + " lon: " + lon);
        System.out.println("initalizing views");

        // find views
        TextView titleText = findViewById(R.id.textView7);
        TextView coordText = findViewById(R.id.textView8);
        WebView webView = findViewById(R.id.webView);

        // set text
        titleText.setText(cityName + " map");
        coordText.setText("lat: " + lat + ",lon: " + lon);

        // google map embedded url building string
        String mapUrl = "https://maps.google.com/maps?q=" + lat + "," + lon + "&t=&z=15&ie=UTF8&iwloc=&output=embed";

        // basic webview setup stuff
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // this makes sure it opens inside the app and doesn't launch a browser
        webView.setWebViewClient(new WebViewClient());

        System.out.println("loading url: " + mapUrl);

        // need to hard code google ifarame as html
        String htmlDocs = "<iframe width=\"100%\" height=\"100%\" style=\"border:0\" src=\"" + mapUrl + "\"></iframe>";
        webView.loadData(htmlDocs, "text/html", "utf-8");
        // webView.loadUrl(mapUrl);
    }
}
