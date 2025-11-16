package com.example.naive_bayes_classifier.model;

import java.util.HashMap;
import java.util.Map;

public class EvaluationMetrics {

    private double accuracy;
    private Map<String, Double> precision;
    private Map<String, Double> recall;
    private Map<String, Double> f1Score;
    private int[][] confusionMatrix;
    private Map<String, Integer> categoryIndices;

    public EvaluationMetrics() {
        this.precision = new HashMap<>();
        this.recall = new HashMap<>();
        this.f1Score = new HashMap<>();
        this.categoryIndices = new HashMap<>();
    }

    // Getters and Setters
    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public Map<String, Double> getPrecision() {
        return precision;
    }

    public void setPrecision(Map<String, Double> precision) {
        this.precision = precision;
    }

    public Map<String, Double> getRecall() {
        return recall;
    }

    public void setRecall(Map<String, Double> recall) {
        this.recall = recall;
    }

    public Map<String, Double> getF1Score() {
        return f1Score;
    }

    public void setF1Score(Map<String, Double> f1Score) {
        this.f1Score = f1Score;
    }

    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    public void setConfusionMatrix(int[][] confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
    }

    public Map<String, Integer> getCategoryIndices() {
        return categoryIndices;
    }

    public void setCategoryIndices(Map<String, Integer> categoryIndices) {
        this.categoryIndices = categoryIndices;
    }

    /**
     * Calculate average metrics
     */
    public Map<String, Double> getMacroAverages() {
        Map<String, Double> averages = new HashMap<>();

        double avgPrecision = precision.values().stream()
                .mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgRecall = recall.values().stream()
                .mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgF1 = f1Score.values().stream()
                .mapToDouble(Double::doubleValue).average().orElse(0.0);

        averages.put("precision", avgPrecision);
        averages.put("recall", avgRecall);
        averages.put("f1Score", avgF1);

        return averages;
    }
}