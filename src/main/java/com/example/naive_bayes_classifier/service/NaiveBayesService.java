package com.example.naive_bayes_classifier.service;



import com.example.naive_bayes_classifier.model.ClassificationResult;
import com.example.naive_bayes_classifier.model.TrainingDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class NaiveBayesService {

    private static final Logger logger = LoggerFactory.getLogger(NaiveBayesService.class);

    @Autowired
    private TextPreprocessingService preprocessingService;

    // Naive Bayes Model Parameters
    private Map<String, Integer> categoryDocumentCount = new HashMap<>();
    private Map<String, Map<String, Integer>> categoryWordCount = new HashMap<>();
    private Map<String, Integer> categoryTotalWords = new HashMap<>();
    private Set<String> vocabulary = new HashSet<>();
    private int totalDocuments = 0;
    private boolean isTrained = false;

    /**
     * Train the Naive Bayes classifier
     * P(C|D) = P(D|C) * P(C) / P(D)
     * Using log probabilities to avoid underflow
     */
    public void train(List<TrainingDocument> documents) {
        logger.info("Starting Naive Bayes training with {} documents", documents.size());

        // Reset model
        reset();

        // Process each training document
        for (TrainingDocument doc : documents) {
            String category = doc.getCategory();

            // Preprocess text using SAFAR
            List<String> stems = preprocessingService.preprocess(doc.getContent());

            if (stems.isEmpty()) {
                logger.warn("Empty document after preprocessing for category: {}", category);
                continue;
            }

            // Update category document count
            categoryDocumentCount.put(category,
                    categoryDocumentCount.getOrDefault(category, 0) + 1);

            // Initialize category if needed
            if (!categoryWordCount.containsKey(category)) {
                categoryWordCount.put(category, new HashMap<>());
                categoryTotalWords.put(category, 0);
            }

            // Count words in category
            Map<String, Integer> wordCount = categoryWordCount.get(category);
            for (String stem : stems) {
                wordCount.put(stem, wordCount.getOrDefault(stem, 0) + 1);
                categoryTotalWords.put(category, categoryTotalWords.get(category) + 1);
                vocabulary.add(stem);
            }

            totalDocuments++;
        }

        isTrained = true;

        logger.info("Training completed successfully:");
        logger.info("  - Total documents: {}", totalDocuments);
        logger.info("  - Vocabulary size: {}", vocabulary.size());
        logger.info("  - Categories: {}", categoryDocumentCount.keySet());

        for (String category : categoryDocumentCount.keySet()) {
            logger.info("  - Category '{}': {} documents, {} words",
                    category,
                    categoryDocumentCount.get(category),
                    categoryTotalWords.get(category));
        }
    }

    /**
     * Classify a text using Naive Bayes algorithm
     */
    public ClassificationResult classify(String text) {
        if (!isTrained) {
            throw new IllegalStateException("Model not trained yet! Please train the model first.");
        }

        logger.info("Classifying text...");

        // Preprocess text using SAFAR
        List<String> stems = preprocessingService.preprocess(text);

        if (stems.isEmpty()) {
            logger.warn("No stems found after preprocessing");
            return createDefaultResult();
        }

        // Calculate log probabilities for each category
        Map<String, Double> logProbabilities = new HashMap<>();

        for (String category : categoryDocumentCount.keySet()) {
            double logProb = calculateLogProbability(category, stems);
            logProbabilities.put(category, logProb);
            logger.debug("Category '{}': log probability = {}", category, logProb);
        }

        // Find best category
        String predictedCategory = Collections.max(
                logProbabilities.entrySet(),
                Map.Entry.comparingByValue()
        ).getKey();

        // Convert to normalized probabilities
        Map<String, Double> probabilities = normalizeProbabilities(logProbabilities);

        double confidence = probabilities.get(predictedCategory);

        ClassificationResult result = new ClassificationResult(
                predictedCategory,
                probabilities,
                confidence
        );
        result.setTotalTokens(stems.size());
        result.setUniqueTokens(new HashSet<>(stems).size());

        logger.info("Classification result: {} (confidence: {:.2f}%)",
                predictedCategory, confidence * 100);

        return result;
    }

    /**
     * Calculate log P(Category | Document)
     * log P(C|D) = log P(C) + Σ log P(w|C) for all words w in D
     */
    private double calculateLogProbability(String category, List<String> words) {
        // Prior probability: P(Category)
        double logPrior = Math.log(
                (double) categoryDocumentCount.get(category) / totalDocuments
        );

        // Likelihood: P(Words | Category)
        double logLikelihood = 0.0;
        Map<String, Integer> wordCount = categoryWordCount.get(category);
        int totalWordsInCategory = categoryTotalWords.get(category);
        int vocabSize = vocabulary.size();

        // Calculate log probability for each word
        for (String word : words) {
            // Laplace smoothing: (count + 1) / (total + vocab_size)
            int count = wordCount.getOrDefault(word, 0);
            double probability = (count + 1.0) / (totalWordsInCategory + vocabSize);
            logLikelihood += Math.log(probability);
        }

        return logPrior + logLikelihood;
    }

    /**
     * Convert log probabilities to normalized probabilities (0-1)
     */
    private Map<String, Double> normalizeProbabilities(Map<String, Double> logProbs) {
        Map<String, Double> normalized = new HashMap<>();

        // Find max log probability for numerical stability
        double maxLogProb = Collections.max(logProbs.values());

        // Convert to exponential and calculate sum
        Map<String, Double> expProbs = new HashMap<>();
        double sum = 0.0;

        for (Map.Entry<String, Double> entry : logProbs.entrySet()) {
            double expProb = Math.exp(entry.getValue() - maxLogProb);
            expProbs.put(entry.getKey(), expProb);
            sum += expProb;
        }

        // Normalize to get probabilities
        for (Map.Entry<String, Double> entry : expProbs.entrySet()) {
            normalized.put(entry.getKey(), entry.getValue() / sum);
        }

        return normalized;
    }

    /**
     * Create default result when classification fails
     */
    private ClassificationResult createDefaultResult() {
        String firstCategory = categoryDocumentCount.keySet().iterator().next();
        Map<String, Double> probs = new HashMap<>();
        for (String cat : categoryDocumentCount.keySet()) {
            probs.put(cat, 1.0 / categoryDocumentCount.size());
        }
        return new ClassificationResult(firstCategory, probs, 1.0 / categoryDocumentCount.size());
    }

    /**
     * Reset the model
     */
    private void reset() {
        categoryDocumentCount.clear();
        categoryWordCount.clear();
        categoryTotalWords.clear();
        vocabulary.clear();
        totalDocuments = 0;
        isTrained = false;
    }

    /**
     * Get model statistics
     */
    public Map<String, Object> getModelStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("trained", isTrained);
        stats.put("totalDocuments", totalDocuments);
        stats.put("vocabularySize", vocabulary.size());
        stats.put("categories", new ArrayList<>(categoryDocumentCount.keySet()));
        stats.put("categoryDocumentCount", categoryDocumentCount);
        stats.put("categoryWordCount", categoryTotalWords);
        stats.put("preprocessingStats", preprocessingService.getStats());
        return stats;
    }

    /**
     * Check if model is trained
     */
    public boolean isTrained() {
        return isTrained;
    }

    /**
     * Get category prior probabilities
     */
    public Map<String, Double> getCategoryPriors() {
        Map<String, Double> priors = new HashMap<>();
        for (String category : categoryDocumentCount.keySet()) {
            priors.put(category,
                    (double) categoryDocumentCount.get(category) / totalDocuments);
        }
        return priors;
    }
}