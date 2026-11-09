package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    Button btnSignIn, btnSignUp;
    EditText etThemePrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Connect variables
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        etThemePrompt = findViewById(R.id.etThemePrompt);

        btnSignUp.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String themePrompt = etThemePrompt.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty() || themePrompt.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);

            if (prefs.contains(user)) {
                Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit().putString(user, pass).putString(user + "_theme", themePrompt).apply();
                Toast.makeText(this, "Account created! You can now sign in.", Toast.LENGTH_SHORT).show();
            }
        });

        // Check sign in account
        btnSignIn.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);
            String savedPass = prefs.getString(user, null);

            if (savedPass != null && savedPass.equals(pass)) {
                // Save their layout choice - again doesn't change layout yet
                String themePrompt = prefs.getString(user + "_theme", "default theme");

                // Go to main
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("username", user);
                intent.putExtra("themePrompt", themePrompt);
                startActivity(intent);
                finish(); // closes login screen (disables the android return button)
            } else {
                Toast.makeText(this, "Wrong username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
