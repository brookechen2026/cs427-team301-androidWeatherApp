package edu.uiuc.cs427app.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WeatherApiClient {
    @POST("v1beta/models/gemini-3.1-flash-lite-preview:generateContent")
    Call<GeminiResponse> getInsight(
            @Query("key") String apiKey,
            @Body GeminiRequest request
    );
}