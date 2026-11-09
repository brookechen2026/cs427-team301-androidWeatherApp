package edu.uiuc.cs427app.locations;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public class WeatherApiRequest {
    private static final String API_KEY = "API_KEY";
    private static final int TIMEOUT_MS = 5000;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static class WeatherData {
        public String cityName;
        public double temperature;
        public String description;
        public int humidity;
        public double windSpeed;
        public double windDeg;
        // added latitude and longitude for map part
        public String lat;
        public String lon;
    }

    public interface WeatherCallback {
        void weatherDataSuccess(WeatherData data);

        void weatherDataFail(String errorMessage);
    }

    public String getWeatherData(String city) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q="
                    + URLEncoder.encode(city.trim(), "UTF-8") + "&appid=" + API_KEY + "&units=imperial");

            conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null)
                    response.append(line);
                in.close();

                JSONObject json = new JSONObject(response.toString());
                double temp = json.getJSONObject("main").getDouble("temp");
                int humidity = json.getJSONObject("main").getInt("humidity");
                String condition = json.getJSONArray("weather").getJSONObject(0).getString("description");
                double wind = json.getJSONObject("wind").getDouble("speed");

                return String.format("Temp: %.1f°F, Condition: %s, Humidity: %d%%, Wind: %.1f mph", temp, condition,
                        humidity, wind);
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return null;
    }

    // city name input has to be city, state, country ex: Chicago, Il, US as
    // duplicates can exist with city names
    public boolean isValidCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return false;
        } else {
            HttpURLConnection conn = null;
            try {
                // basic openweather api request by city name only for US states
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q="
                        + URLEncoder.encode(city.trim(), "UTF-8") + "&appid=" + API_KEY);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(TIMEOUT_MS);
                conn.setReadTimeout(TIMEOUT_MS);

                return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                android.util.Log.e("API_TEST", "Exception caught: " + e.getMessage());
                return false;
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
        }
    }

    // this function gathers the weather data from openweatherapp api and returns
    // weatherdata class
    private WeatherData getWeatherDataInfo(String city) {
        // checks weather the city input is null or not
        if (city == null || city.trim().isEmpty()) {
            return null;
        } else {
            HttpURLConnection conn = null;
            try {
                // api request for weather data
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?units=imperial&q="
                        + URLEncoder.encode(city.trim(), "UTF-8") + "&appid=" + API_KEY);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(TIMEOUT_MS);
                conn.setReadTimeout(TIMEOUT_MS);

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // parses the json returned from api to store in weatherdata class

                JSONObject json = new JSONObject(response.toString());

                WeatherData data = new WeatherData();

                data.cityName = json.getString("name");
                data.temperature = json.getJSONObject("main").getDouble("temp");
                data.humidity = json.getJSONObject("main").getInt("humidity");
                data.windSpeed = json.getJSONObject("wind").getDouble("speed");
                data.windDeg = json.getJSONObject("wind").optDouble("deg", 0);
                data.description = json.getJSONArray("weather").getJSONObject(0).getString("description");

                System.out.println("got basic data");
                // print to see if basic data works
                // getting coordinates now
                data.lat = String.valueOf(json.getJSONObject("coord").getDouble("lat"));
                data.lon = String.valueOf(json.getJSONObject("coord").getDouble("lon"));
                System.out.println("lat: " + data.lat + " lon: " + data.lon);

                return data;

            } catch (Exception e) {
                return null;
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
        }
    }

    // this is the function called by ui code to recieve weather data.
    // callback is is the weather data object upon success and error upon failure
    public void getWeatherUICall(String city, WeatherCallback callback) {
        executor.execute(() -> {
            // calls the weather data function to get info about city
            WeatherData data = getWeatherDataInfo(city);

            // if data was null, return error else it returns the Weather data object.
            if (data == null) {
                callback.weatherDataFail("Could not display weather data. Error.");
            } else {
                callback.weatherDataSuccess(data);
            }
        });
    }

}