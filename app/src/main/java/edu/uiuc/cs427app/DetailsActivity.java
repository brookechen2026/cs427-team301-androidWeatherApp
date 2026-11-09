package edu.uiuc.cs427app;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.content.Intent;
import edu.uiuc.cs427app.service.*;
import edu.uiuc.cs427app.locations.WeatherApiRequest;
import java.util.Date;
import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private WeatherInsightService weatherService = new WeatherInsightService();
    private WeatherApiRequest.WeatherData currentWeatherData;
    private String currentCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Process the Intent payload that has opened this Activity and show the
        // information accordingly
        String cityName = getIntent().getStringExtra("city").toString();
        String welcome = "Welcome to the " + cityName;
        String cityWeatherInfo = "Detailed information about the weather of " + cityName;

        // Initializing the GUI elements
        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView cityInfoMessage = findViewById(R.id.cityInfo);

        welcomeMessage.setText(welcome);
        cityInfoMessage.setText("Loading weather for " + cityName + ".......");

        System.out.println("Trying to get weather request here");
        // doing weather api request
        WeatherApiRequest weatherReq = new WeatherApiRequest();
        weatherReq.getWeatherUICall(cityName, new WeatherApiRequest.WeatherCallback() {
            @Override
            public void weatherDataSuccess(WeatherApiRequest.WeatherData data) {
                System.out.println("it worked!!");
                currentWeatherData = data;
                runOnUiThread(() -> {
                    // getting time and date using util date since simple
                    Date currentDate = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateTimeString = sdf.format(currentDate);
                    // fix up naming here
                    String weatherInfo = "Date and Time:: " + dateTimeString + "\n" +
                            "TEMPERATURE:: " + data.temperature + " F\n" +
                            "WEATHER: " + data.description + "\n" +
                            "HUMIDITY:: " + data.humidity + "%\n" +
                            "WIND SPEED: " + data.windSpeed + " mph\n";
                    cityInfoMessage.setText(weatherInfo);
                });
            }

            @Override
            public void weatherDataFail(String errorMessage) {
                System.out.println("it failed... " + errorMessage);
                runOnUiThread(() -> {
                    cityInfoMessage.setText(errorMessage);
                });
            }
        });

        Button buttonMap = findViewById(R.id.mapButton);
        Button buttonInsight = findViewById(R.id.buttonWeatherInsight);

        buttonInsight.setOnClickListener(v -> {
            System.out.println("Clicked insight button!!!");
            if (currentWeatherData == null) {
                Toast.makeText(this, "Wait for weather data to load............", Toast.LENGTH_SHORT).show();
                return;
            }

            // go to new activity for insights instead of doing it here
            Intent intent = new Intent(this, InsightActivity.class);
            // passing data over
            intent.putExtra("cityName", currentWeatherData.cityName);
            intent.putExtra("temperature", currentWeatherData.temperature);
            intent.putExtra("description", currentWeatherData.description);
            startActivity(intent);
        });

        buttonMap.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        // Implement this (create an Intent that goes to a new Activity, which shows the
        // map)
        if (view.getId() == R.id.mapButton) {
            System.out.println("going to map.....");
            if (currentWeatherData == null || currentWeatherData.lat == null) {
                Toast.makeText(this, "Wait for weather data load...", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("cityName", currentWeatherData.cityName);
            intent.putExtra("lat", currentWeatherData.lat);
            intent.putExtra("lon", currentWeatherData.lon);
            startActivity(intent);
        }
    }
}
