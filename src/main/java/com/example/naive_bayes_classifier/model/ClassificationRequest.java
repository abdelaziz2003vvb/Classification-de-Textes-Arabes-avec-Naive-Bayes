package com.example.naive_bayes_classifier.model;


public class ClassificationRequest {
    private String text;

    public ClassificationRequest() {}

    public ClassificationRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}