package com.example.naive_bayes_classifier.utils;


import com.example.naive_bayes_classifier.model.EvaluationMetrics;
import com.example.naive_bayes_classifier.model.TrainingDocument;
import com.example.naive_bayes_classifier.service.NaiveBayesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MetricsCalculator {

    private static final Logger logger = LoggerFactory.getLogger(MetricsCalculator.class);

    /**
     * Evaluate model on test set
     */
    public EvaluationMetrics evaluate(NaiveBayesService model, List<TrainingDocument> testSet) {
        logger.info("Evaluating model on {} test documents", testSet.size());

        EvaluationMetrics metrics = new EvaluationMetrics();

        // Get all categories
        Set<String> categories = new LinkedHashSet<>();
        for (TrainingDocument doc : testSet) {
            categories.add(doc.getCategory());
        }

        // Create category to index mapping
        Map<String, Integer> categoryIndices = new HashMap<>();
        int index = 0;
        for (String category : categories) {
            categoryIndices.put(category, index++);
        }
        metrics.setCategoryIndices(categoryIndices);

        // Initialize confusion matrix
        int numCategories = categories.size();
        int[][] confusionMatrix = new int[numCategories][numCategories];

        // Classify each test document
        int correct = 0;
        for (TrainingDocument doc : testSet) {
            String actual = doc.getCategory();
            String predicted = model.classify(doc.getContent()).getPredictedCategory();

            if (actual.equals(predicted)) {
                correct++;
            }

            // Update confusion matrix
            int actualIdx = categoryIndices.get(actual);
            int predictedIdx = categoryIndices.get(predicted);
            confusionMatrix[actualIdx][predictedIdx]++;
        }

        // Calculate accuracy
        double accuracy = (double) correct / testSet.size();
        metrics.setAccuracy(accuracy);
        metrics.setConfusionMatrix(confusionMatrix);

        // Calculate precision, recall, F1 for each category
        for (String category : categories) {
            int idx = categoryIndices.get(category);

            // True Positives
            int tp = confusionMatrix[idx][idx];

            // False Positives (column sum - TP)
            int fp = 0;
            for (int i = 0; i < numCategories; i++) {
                if (i != idx) fp += confusionMatrix[i][idx];
            }

            // False Negatives (row sum - TP)
            int fn = 0;
            for (int j = 0; j < numCategories; j++) {
                if (j != idx) fn += confusionMatrix[idx][j];
            }

            // Calculate metrics
            double precision = (tp + fp > 0) ? (double) tp / (tp + fp) : 0.0;
            double recall = (tp + fn > 0) ? (double) tp / (tp + fn) : 0.0;
            double f1 = (precision + recall > 0) ?
                    2 * precision * recall / (precision + recall) : 0.0;

            metrics.getPrecision().put(category, precision);
            metrics.getRecall().put(category, recall);
            metrics.getF1Score().put(category, f1);

            logger.info("Category '{}': P={:.3f}, R={:.3f}, F1={:.3f}",
                    category, precision, recall, f1);
        }

        logger.info("Overall Accuracy: {:.2f}%", accuracy * 100);

        return metrics;
    }
}