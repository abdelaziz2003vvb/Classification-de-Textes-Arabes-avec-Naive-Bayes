package com.example.naive_bayes_classifier.model;

import java.util.Map;

public class ClassificationResult {
    private String predictedCategory;
    private Map<String, Double> probabilities;
    private double confidence;
    private int totalTokens;
    private int uniqueTokens;

    public ClassificationResult() {}

    public ClassificationResult(String predictedCategory,
                                Map<String, Double> probabilities,
                                double confidence) {
        this.predictedCategory = predictedCategory;
        this.probabilities = probabilities;
        this.confidence = confidence;
    }

    // Getters and Setters
    public String getPredictedCategory() {
        return predictedCategory;
    }

    public void setPredictedCategory(String predictedCategory) {
        this.predictedCategory = predictedCategory;
    }

    public Map<String, Double> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(Map<String, Double> probabilities) {
        this.probabilities = probabilities;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }

    public int getUniqueTokens() {
        return uniqueTokens;
    }

    public void setUniqueTokens(int uniqueTokens) {
        this.uniqueTokens = uniqueTokens;
    }
}