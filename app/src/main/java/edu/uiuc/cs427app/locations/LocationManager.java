package edu.uiuc.cs427app.locations;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;


public class LocationManager {
    private final WeatherApiRequest weatherApiRequest;
    private final Context context;
    private final Handler mainHandler;

    public LocationManager(Context context) {
        this.context           = context.getApplicationContext();
        this.weatherApiRequest = new WeatherApiRequest();
        this.mainHandler       = new Handler(Looper.getMainLooper());
    }

    // returns list of cities for a specific user given username
    public List<String> getCities(String username) {
        return loadCities(username);
    }

    // adds city for username with given input city, username, and handles input errors before callback
    public void addCity(String city, String username, LocationCallback callback) {
        String input = city.trim();
        new Thread(() -> {

            // error handling for city stirng
            if (input.isEmpty()) {
                mainHandler.post(() -> callback.citiesUpdateFail("City name is empty. Please enter a valid city name (City, State, Country) for US or (City, Country) for non US."));
                return;
            }

            if (!weatherApiRequest.isValidCity(input)) {
                mainHandler.post(() -> callback.citiesUpdateFail("City inputed is invalid.Please enter a valid city name (City, State, Country) for US or (City, Country) for non US.\")"));
                return;
            }

            if (loadCities(username).contains(input)) {
                mainHandler.post(() -> callback.citiesUpdateFail("City is already in list."));
                return;
            }

            // adding city in sqlite database via manager
            ContentValues values = new ContentValues();
            values.put(DatabaseManager.USERNAME, username);
            values.put(DatabaseManager.LOCATION, input);
            context.getContentResolver().insert(DatabaseManager.CONTENT_URI, values);

            mainHandler.post(() -> callback.citiesUpdated(loadCities(username)));

        }).start();
    }

    // removes city from username given the city and returns the updated cities
    public void removeCity(String city, String username, LocationCallback callback) {
        new Thread(() -> {

            int city_removed = context.getContentResolver().delete(DatabaseManager.CONTENT_URI, DatabaseManager.USERNAME + " = ? AND " + DatabaseManager.LOCATION + " = ?", new String[]{ username, city });

            // if it returns as no city removed, return error message
            if (city_removed == 0) {
                mainHandler.post(() -> callback.citiesUpdateFail("The city entered was not able to removed from list. Check the city entered again."));
                return;
            }

            mainHandler.post(() -> callback.citiesUpdated(loadCities(username)));

        }).start();
    }

   // loads all the cities from sqlite and converts to list for user city list return
    private List<String> loadCities(String username) {
        List<String> cityList = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(DatabaseManager.CONTENT_URI, new String[]{ DatabaseManager.LOCATION }, DatabaseManager.USERNAME + " = ?", new String[]{ username }, null);
        // iterates over the rows of list of cities to append to cityList using cursor
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    cityList.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseManager.LOCATION)));
                }
            }
            finally {
                cursor.close();
            }
        }

        return cityList;
    }
}
