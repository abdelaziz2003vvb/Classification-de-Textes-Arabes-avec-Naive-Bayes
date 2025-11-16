package com.example.naive_bayes_classifier.service;


import com.example.naive_bayes_classifier.model.TrainingDocument;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);
    private static final String TRAINING_DATA_PATH = "Data";

    /**
     * Load all training data from directory
     */
    public List<TrainingDocument> loadTrainingData() {
        List<TrainingDocument> documents = new ArrayList<>();

        try {
            Path dataPath = Paths.get(TRAINING_DATA_PATH);

            if (!Files.exists(dataPath) || !Files.isDirectory(dataPath)) {
                logger.warn("Training data directory not found: {}", TRAINING_DATA_PATH);
                logger.info("Please create '{}' directory and add .txt files", TRAINING_DATA_PATH);
                return documents;
            }

            Files.list(dataPath)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .forEach(path -> {
                        try {
                            String content = Files.readString(path, StandardCharsets.UTF_8);
                            String filename = path.getFileName().toString();
                            String category = filename.replace(".txt", "");

                            if (content.trim().isEmpty()) {
                                logger.warn("Empty file: {}", filename);
                                return;
                            }

                            documents.add(new TrainingDocument(category, content));
                            logger.info("Loaded: {} (category: {}, {} chars)",
                                    filename, category, content.length());

                        } catch (IOException e) {
                            logger.error("Error reading file: {}", path, e);
                        }
                    });

            logger.info("Successfully loaded {} training documents", documents.size());

        } catch (IOException e) {
            logger.error("Error accessing training data directory", e);
        }

        return documents;
    }

    /**
     * Split documents into train and test sets
     * @param documents All documents
     * @param testRatio Percentage for test (e.g., 0.2 for 20%)
     */
    public Map<String, List<TrainingDocument>> splitTrainTest(
            List<TrainingDocument> documents, double testRatio) {

        Collections.shuffle(documents);

        int testSize = (int) (documents.size() * testRatio);
        int trainSize = documents.size() - testSize;

        List<TrainingDocument> trainSet = new ArrayList<>(documents.subList(0, trainSize));
        List<TrainingDocument> testSet = new ArrayList<>(documents.subList(trainSize, documents.size()));

        Map<String, List<TrainingDocument>> split = new HashMap<>();
        split.put("train", trainSet);
        split.put("test", testSet);

        logger.info("Split: {} training, {} test documents", trainSize, testSize);

        return split;
    }

    /**
     * Add a training document
     */
    public void addTrainingDocument(String category, String content, String filename) {
        try {
            Path dataPath = Paths.get(TRAINING_DATA_PATH);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }

            Path filePath = dataPath.resolve(filename);
            Files.writeString(filePath, content, StandardCharsets.UTF_8);

            logger.info("Added training document: {} (category: {})", filename, category);

        } catch (IOException e) {
            logger.error("Error saving training document", e);
            throw new RuntimeException("Failed to save training document", e);
        }
    }

    /**
     * Get training data statistics
     */
    public Map<String, Object> getDataStats() {
        List<TrainingDocument> documents = loadTrainingData();

        Map<String, Integer> categoryCount = new HashMap<>();
        int totalWords = 0;

        for (TrainingDocument doc : documents) {
            categoryCount.put(doc.getCategory(),
                    categoryCount.getOrDefault(doc.getCategory(), 0) + 1);
            totalWords += doc.getContent().split("\\s+").length;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDocuments", documents.size());
        stats.put("categories", categoryCount);
        stats.put("averageWordsPerDocument",
                documents.isEmpty() ? 0 : totalWords / documents.size());

        return stats;
    }
}