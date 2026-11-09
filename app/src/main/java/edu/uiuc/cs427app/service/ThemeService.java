package edu.uiuc.cs427app.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

// handles calling gemini api for generating theme colors
public class ThemeService {
    // gemini api key
    private static final String API_KEY = "API-KEY";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite-preview:generateContent?key=";
    // defualt colors if api call fails
    public String backgroundColor = "#FFFFFF";
    public String textColor = "#000000";
    public String buttonColor = "#6200EE";

    // calls gemini to generate theme colros based on a user description
    public void fetchTheme(String description) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(GEMINI_URL + API_KEY);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10030);
            conn.setReadTimeout(10030);
            // promt tells gemini to return json with color hex codes
            String prompt = "Generate a color theme for a mobile app based on this description: \""
                    + description + "\". Return ONLY a JSON object with these exact fields: "
                    + "backgroundColor, textColor, buttonColor. All values must be hex color codes "
                    + "starting with #. Do not include any other text or markdown formatting.";
            // use the existing gemini request to build body
            GeminiRequest request = new GeminiRequest(prompt);
            Gson gson = new Gson();
            String body = gson.toJson(request);
            // write request body
            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.close();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // read the respnose
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                // parse gemini response wrapper to get the actual text
                GeminiResponse resp = gson.fromJson(sb.toString(), GeminiResponse.class);
                String text = resp.getResponseText();
                // somtimes gemini wraps response in ```json``` so strip that
                text = text.replace("```json", "").replace("```", "").trim();
                // pull out color values from json
                JsonObject colors = new JsonParser().parse(text).getAsJsonObject();
                backgroundColor = colors.get("backgroundColor").getAsString();
                textColor = colors.get("textColor").getAsString();
                buttonColor = colors.get("buttonColor").getAsString();
            }
        } catch (Exception e) {
            // if anything goes wrong we just keep the defualts
            android.util.Log.e("ThemeService", "theme fetch failed: " + e.getMessage());
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }
}
