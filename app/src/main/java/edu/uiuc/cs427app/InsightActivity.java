package edu.uiuc.cs427app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import edu.uiuc.cs427app.service.WeatherInsightService;

public class InsightActivity extends AppCompatActivity {

    private WeatherInsightService insightService;
    private String cityName = "";
    private double temp = 0.0;
    private String desc = "";
    private int attemptCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insight);

        insightService = new WeatherInsightService();

        // getting passed data
        cityName = getIntent().getStringExtra("cityName");
        temp = getIntent().getDoubleExtra("temperature", 0.0);
        desc = getIntent().getStringExtra("description");

        TextView loadingText = findViewById(R.id.textView3);
        Button btn1 = findViewById(R.id.button3);
        Button btn2 = findViewById(R.id.button4);
        TextView answerText = findViewById(R.id.textView4);

        System.out.println("fetching questions for " + cityName);

        // getting the questions
        insightService.fetchQuestions(cityName, temp, desc).thenAccept(questions -> {
            System.out.println("got questions back from service");
            runOnUiThread(() -> {
                loadingText.setText("choose a question below:");

                // set buttons with strings
                btn1.setText(questions[0]);
                btn2.setText(questions[1]);

                // make visible now
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
            });
        });

        // click listeners to get answer
        btn1.setOnClickListener(v -> {
            String q = btn1.getText().toString();
            answerText.setText("asking ai: " + q + "......");
            insightService.fetchAnswer(q, cityName, temp, desc).thenAccept(answer -> {
                runOnUiThread(() -> {
                    answerText.setText(answer);
                });
            });
        });

        btn2.setOnClickListener(v -> {
            String q = btn2.getText().toString();
            answerText.setText("asking ai: " + q + ".......");
            insightService.fetchAnswer(q, cityName, temp, desc).thenAccept(answer -> {
                runOnUiThread(() -> {
                    answerText.setText(answer);
                });
            });
        });
    }
}
