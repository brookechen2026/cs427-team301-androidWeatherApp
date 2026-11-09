package edu.uiuc.cs427app.service;
import java.util.ArrayList;
import java.util.List;

public class GeminiRequest {
    public List<Content> contents;

    public GeminiRequest(String text) {
        this.contents = new ArrayList<>();
        Content content = new Content();
        content.parts = new ArrayList<>();
        Part part = new Part();
        part.text = text;
        content.parts.add(part);
        this.contents.add(content);
    }

    public static class Content { public List<Part> parts; }
    public static class Part { public String text; }
}