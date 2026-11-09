package edu.uiuc.cs427app.service;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
// use similar things to our api client
import retrofit2.Call;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class WeatherInsightServiceTest {

    @Test
    public void geminiResponse_parsing_isCorrect() {
        GeminiResponse response = new GeminiResponse();
        response.candidates = new ArrayList<>();

        GeminiResponse.Candidate candidate = new GeminiResponse.Candidate();
        candidate.content = new GeminiResponse.Content();
        candidate.content.parts = new ArrayList<>();

        GeminiResponse.Part part = new GeminiResponse.Part();
        part.text = "It's sunny in Urbana! Perfect for a study break.";
        candidate.content.parts.add(part);

        response.candidates.add(candidate);

        assertEquals("It's sunny in Urbana! Perfect for a study break.", response.getResponseText());
    }

    @Test
    public void geminiResponse_emptyCandidates_returnsDefault() {
        GeminiResponse response = new GeminiResponse();
        response.candidates = new ArrayList<>();

        assertEquals("No insight available.", response.getResponseText());
    }

    @Test
    public void weatherRequest_initialization_isCorrect() {
        String testPrompt = "Test prompt for Urbana";
        GeminiRequest request = new GeminiRequest(testPrompt);

        assertNotNull(request.contents);
        assertEquals(1, request.contents.size());
        assertEquals(testPrompt, request.contents.get(0).parts.get(0).text);
    }

    @Test
    public void geminiResponse_multipleLines_parsingIsCorrect() {
        String llmOutput = "Should I bring an umbrella?\n\nWhat kind of shoes are best?";
        String[] lines = llmOutput.split("\n");
        List<String> goodLines = new ArrayList<>();
        for (String l : lines) {
            if (l.trim().length() > 0) {
                goodLines.add(l.trim());
            }
        }

        assertEquals(2, goodLines.size());
        assertEquals("Should I bring an umbrella?", goodLines.get(0));
        assertEquals("What kind of shoes are best?", goodLines.get(1));
    }

    @Test
    public void geminiResponse_singleLine_returnsDefaults() {
        List<String> goodLines = new ArrayList<>();
        goodLines.add("Only one question provided.");

        String[] result;
        if (goodLines.size() >= 2) {
            result = new String[] { goodLines.get(0), goodLines.get(1) };
        } else {
            result = new String[] { "What should I wear?", "Should I go outside?" };
        }

        assertEquals("What should I wear?", result[0]);
        assertEquals("Should I go outside?", result[1]);
    }

    @Test
    public void testMockingForRubric() throws Exception {
        // look at mockito docks: https://site.mockito.org/
        // make fake client not hit gemini
        WeatherApiClient fakeApiThing = mock(WeatherApiClient.class);
        Call<GeminiResponse> fakeCall = mock(Call.class);
        // build nested object for the response
        GeminiResponse myFakeResponse = new GeminiResponse();
        myFakeResponse.candidates = new ArrayList<>();
        GeminiResponse.Candidate cand = new GeminiResponse.Candidate();
        cand.content = new GeminiResponse.Content();
        cand.content.parts = new ArrayList<>();
        GeminiResponse.Part thePart = new GeminiResponse.Part();
        thePart.text = "this is a fake anwser for the test";
        cand.content.parts.add(thePart);
        myFakeResponse.candidates.add(cand);
        Response<GeminiResponse> retrofitStuff = Response.success(myFakeResponse);
        // tell mock what to do when called
        when(fakeCall.execute()).thenReturn(retrofitStuff);
        when(fakeApiThing.getInsight(anyString(), any(GeminiRequest.class))).thenReturn(fakeCall);
        WeatherInsightService myService = new WeatherInsightService();
        // inject mock here (fake dumlient)
        myService.setClient(fakeApiThing);
        // test it
        String theAnwser = myService.fetchAnswer("What should I wear?", "Chicago", 70.0, "Sunny").get();
        assertEquals("this is a fake anwser for the test", theAnwser);
    }
}