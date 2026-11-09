package edu.uiuc.cs427app;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import edu.uiuc.cs427app.locations.LocationCallback;
import edu.uiuc.cs427app.locations.LocationManager;
import edu.uiuc.cs427app.service.ThemeService;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LinearLayout cityListContainer;
    private String username;
    private ThemeService themeService;

    // store theme colros so we can reapply when list refreshes
    private int currentBgColor = Color.WHITE;
    private int currentTxtColor = Color.BLACK;
    private int currentBtnColor = Color.parseColor("#6200EE");
    private boolean hasTheme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get username and theme promt from the login screen
        username = getIntent().getStringExtra("username");
        if (username == null)
            username = "Unknown";
        String themePrompt = getIntent().getStringExtra("themePrompt");

        // set title to show team number and username
        getSupportActionBar().setTitle("Team 301 - " + username);

        // initalize location manager for city operations
        locationManager = new LocationManager(this);
        themeService = new ThemeService();

        // get ui elements
        cityListContainer = findViewById(R.id.cityListContainer);
        Button buttonNew = findViewById(R.id.buttonAddLocation);
        Button logoutButton = findViewById(R.id.buttonLogout);

        // load the users cities into the list
        refreshCityList();

        // add location button opens dialog box
        buttonNew.setOnClickListener(v -> showAddCityDialog());

        // logout goes back to login screen
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // apply llm theme if user has one
        if (themePrompt != null && !themePrompt.isEmpty()) {
            applyLlmTheme(themePrompt);
        }
    }

    // loads all the cities for this user and builds the list in the ui
    private void refreshCityList() {
        cityListContainer.removeAllViews();
        List<String> cities = locationManager.getCities(username);

        if (cities.isEmpty()) {
            // shows message when user hasnt added any cities yet
            TextView emptyText = new TextView(this);
            emptyText.setText("No cities added yet. Click 'Add a location' to get started!");
            emptyText.setPadding(16, 32, 16, 32);
            if (hasTheme)
                emptyText.setTextColor(currentTxtColor);
            cityListContainer.addView(emptyText);
        } else {
            // create row for each city
            for (String city : cities) {
                addCityRow(city);
            }
        }
    }

    // creates a row in the list for a single city with detail and remove buttons
    private void addCityRow(String cityName) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(8, 8, 8, 8);
        //city name
        TextView cityLabel = new TextView(this);
        cityLabel.setText(cityName);
        cityLabel.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        if (hasTheme)
            cityLabel.setTextColor(currentTxtColor);
        row.addView(cityLabel);

        //Weather button
        Button weatherBtn = new Button(this);
        weatherBtn.setText("Weather");
        weatherBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("city", cityName);
            startActivity(intent);
        });
        if (hasTheme) {
            weatherBtn.setBackgroundColor(currentBtnColor);
            weatherBtn.setTextColor(currentTxtColor);
        }
        row.addView(weatherBtn);
        //Map button
        Button mapBtn = new Button(this);
        mapBtn.setText("Map");
        mapBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("city", cityName);
            startActivity(intent);
        });
        if (hasTheme) {
            mapBtn.setBackgroundColor(currentBtnColor);
            mapBtn.setTextColor(currentTxtColor);
        }
        row.addView(mapBtn);
        //remove Button
        Button removeBtn = new Button(this);
        removeBtn.setText("Remove");
        removeBtn.setOnClickListener(v -> {
            locationManager.removeCity(cityName, username, new LocationCallback() {
                @Override
                public void citiesUpdated(List<String> cities) {
                    refreshCityList();
                }

                @Override
                public void citiesUpdateFail(String errorMessage) {
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        if (hasTheme) {
            removeBtn.setBackgroundColor(currentBtnColor);
            removeBtn.setTextColor(currentTxtColor);
        }

        row.addView(removeBtn);

        cityListContainer.addView(row);
    }

    // shows dialog box asking user to enter a city name
    private void showAddCityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a City");

        // text feild for entering city
        final EditText input = new EditText(this);
        input.setHint("City, State, Country (US) or City, Country");
        builder.setView(input);
        // when user clicks add, validate and add city using location manger
        builder.setPositiveButton("Add", (dialog, which) -> {
            String cityName = input.getText().toString().trim();
            if (cityName.isEmpty()) {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                return;
            }
            locationManager.addCity(cityName, username, new LocationCallback() {
                @Override
                public void citiesUpdated(List<String> cities) {
                    refreshCityList();
                }

                @Override
                public void citiesUpdateFail(String errorMessage) {
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // calls the gemini api to generate theme colors and applys them to the ui
    private void applyLlmTheme(String themePrompt) {
        Toast.makeText(this, "Loading theme: " + themePrompt, Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            // fetch theme on backgrond thread
            themeService.fetchTheme(themePrompt);
            // switch to main thread to update ui
            runOnUiThread(() -> {
                try {
                    Toast.makeText(this, "Theme: bg=" + themeService.backgroundColor
                            + " txt=" + themeService.textColor
                            + " btn=" + themeService.buttonColor, Toast.LENGTH_LONG).show();

                    // parse the colors from theme service
                    currentBgColor = Color.parseColor(themeService.backgroundColor);
                    currentTxtColor = Color.parseColor(themeService.textColor);
                    currentBtnColor = Color.parseColor(themeService.buttonColor);
                    hasTheme = true;

                    // apply backgroud color to the main layout
                    findViewById(R.id.mainLayout).setBackgroundColor(currentBgColor);

                    // apply text colors to header labels
                    TextView title = findViewById(R.id.textViewTitle);
                    TextView listLabel = findViewById(R.id.textViewSubtitle);
                    title.setTextColor(currentTxtColor);
                    listLabel.setTextColor(currentTxtColor);

                    // apply button colors
                    Button addBtn = findViewById(R.id.buttonAddLocation);
                    Button logoutBtn = findViewById(R.id.buttonLogout);
                    addBtn.setBackgroundColor(currentBtnColor);
                    logoutBtn.setBackgroundColor(currentBtnColor);
                    addBtn.setTextColor(currentTxtColor);
                    logoutBtn.setTextColor(currentTxtColor);

                    // refresh city list so rows get themed too
                    refreshCityList();

                } catch (Exception e) {
                    // if color parsing fails just leave defualt colors
                    Toast.makeText(this, "Theme error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    android.util.Log.e("MainActivity", "error applying theme: " + e.getMessage());
                }
            });
        }).start();
    }
}
