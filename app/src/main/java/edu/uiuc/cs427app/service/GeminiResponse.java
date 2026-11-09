package edu.uiuc.cs427app.service;
import java.util.List;

public class GeminiResponse {
    public List<Candidate> candidates;

    public static class Candidate { public Content content; }
    public static class Content { public List<Part> parts; }
    public static class Part { public String text; }

    public String getResponseText() {
        if (candidates != null && !candidates.isEmpty()) {
            List<Part> parts = candidates.get(0).content.parts;
            return parts.get(parts.size() - 1).text;
        }
        return "No insight available.";
    }
}