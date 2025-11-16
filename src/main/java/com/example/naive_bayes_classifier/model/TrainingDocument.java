package com.example.naive_bayes_classifier.model;

public class TrainingDocument {
    private String category;
    private String content;

    public TrainingDocument() {}

    public TrainingDocument(String category, String content) {
        this.category = category;
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}