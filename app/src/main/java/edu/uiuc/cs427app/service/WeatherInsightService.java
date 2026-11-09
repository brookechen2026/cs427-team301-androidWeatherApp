package edu.uiuc.cs427app.service;

import java.util.concurrent.CompletableFuture;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;
import java.util.ArrayList;

public class WeatherInsightService {
    private static final String API_KEY = "API-KEY";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    private WeatherApiClient activeClient;

    // add dynamic client so i can swap out mock and real
    public void setClient(WeatherApiClient client) {
        this.activeClient = client;
    }

    // gets 2 questions from the llm
    public CompletableFuture<String[]> fetchQuestions(String cityName, double temp, String desc) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("fetching questions from llm");
                WeatherApiClient client = this.activeClient;
                if (client == null) {
                    // give some time to wait for gemini
                    okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
                            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    client = retrofit.create(WeatherApiClient.class);
                }

                String prompt = "Today's weather in " + cityName + " is " + temp + " degrees with " + desc + ". " +
                        "Generate exactly two context specific questions users might ask to help them make decisions about their day. "
                        +
                        "Put the questions on separate lines. Do not add any extra text or numbering.";

                GeminiRequest request = new GeminiRequest(prompt);

                Response<GeminiResponse> response = client.getInsight(API_KEY, request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String fullResponse = response.body().getResponseText();
                    System.out.println("llm said: " + fullResponse);

                    // split quesiton by newline
                    String[] lines = fullResponse.split("\n");
                    // filter out empty lines
                    List<String> goodLines = new ArrayList<>();
                    for (String l : lines) {
                        if (l.trim().length() > 0) {
                            goodLines.add(l.trim());
                        }
                    }
                    if (goodLines.size() >= 2) {
                        return new String[] { goodLines.get(0), goodLines.get(1) };
                    } else {
                        return new String[] { "What should I wear?", "Should I go outside?" };
                    }
                } else {
                    return new String[] { "API Error!1!! Code: " + response.code(), "Try this again later..???" };
                }
            } catch (Exception e) {
                return new String[] { "Error getting question 1", "Error getting question 2" };
            }
        });
    }

    // get answer for a specific question someone clicked
    public CompletableFuture<String> fetchAnswer(String question, String cityName, double temp, String desc) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                WeatherApiClient client = this.activeClient;
                if (client == null) {
                    // addded previously but this is to extend how long we wait for gemeini
                    okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
                            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .build();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    client = retrofit.create(WeatherApiClient.class);
                }

                String prompt = "The weather in " + cityName + " is " + temp + "F and " + desc + ". " +
                        "Answer this question in 1 or 2 sentences: " + question;

                GeminiRequest request = new GeminiRequest(prompt);

                Response<GeminiResponse> response = client.getInsight(API_KEY, request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().getResponseText();
                } else {
                    return "Sorry, I could not get an answer for ya. Error code: " + response.code();
                }
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        });
    }
}