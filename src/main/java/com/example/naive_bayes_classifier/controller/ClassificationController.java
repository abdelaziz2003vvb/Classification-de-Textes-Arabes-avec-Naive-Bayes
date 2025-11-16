package com.example.naive_bayes_classifier.controller;

import com.example.naive_bayes_classifier.model.*;
import com.example.naive_bayes_classifier.service.*;
import com.example.naive_bayes_classifier.utils.MetricsCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/classifier")
@CrossOrigin(origins = "*")
public class ClassificationController {

    @Autowired
    private NaiveBayesService naiveBayesService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private MetricsCalculator metricsCalculator;

    /**
     * Ping endpoint
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Naive Bayes Classifier with SAFAR is running!");
    }

    /**
     * Train the classifier model
     */
    @PostMapping("/train")
    public ResponseEntity<?> train() {
        try {
            List<TrainingDocument> documents = trainingService.loadTrainingData();

            if (documents.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", "No training data found",
                                "message", "Please add .txt files under Data/ folder"
                        ));
            }

            naiveBayesService.train(documents);

            return ResponseEntity.ok(Map.of(
                    "message", "Model trained successfully",
                    "stats", naiveBayesService.getModelStats()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Training failed", "message", e.getMessage()));
        }
    }

    /**
     * Train & evaluate model with train/test split
     */
    @PostMapping("/train-evaluate")
    public ResponseEntity<?> trainAndEvaluate(
            @RequestParam(defaultValue = "0.2") double testRatio) {

        try {
            List<TrainingDocument> documents = trainingService.loadTrainingData();

            if (documents.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No training data found"));
            }

            Map<String, List<TrainingDocument>> split =
                    trainingService.splitTrainTest(documents, testRatio);

            List<TrainingDocument> trainSet = split.get("train");
            List<TrainingDocument> testSet = split.get("test");

            naiveBayesService.train(trainSet);

            EvaluationMetrics metrics = metricsCalculator.evaluate(naiveBayesService, testSet);

            return ResponseEntity.ok(Map.of(
                    "message", "Training & evaluation completed",
                    "trainSize", trainSet.size(),
                    "testSize", testSet.size(),
                    "metrics", Map.of(
                            "accuracy", metrics.getAccuracy(),
                            "precision", metrics.getPrecision(),
                            "recall", metrics.getRecall(),
                            "f1Score", metrics.getF1Score(),
                            "macroAverage", metrics.getMacroAverages()
                    ),
                    "confusionMatrix", metrics.getConfusionMatrix(),
                    "categoryIndices", metrics.getCategoryIndices()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Training/Evaluation failed", "message", e.getMessage()));
        }
    }

    /**
     * Classify text
     */
    @PostMapping(
            value = "/classify",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> classify(@RequestBody ClassificationRequest request) {

        try {
            if (request.getText() == null || request.getText().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Text cannot be empty"));
            }

            if (!naiveBayesService.isTrained()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(Map.of(
                                "error", "Model not trained",
                                "message", "Please call /train first"
                        ));
            }

            ClassificationResult result = naiveBayesService.classify(request.getText());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Classification failed",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get trained model statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(naiveBayesService.getModelStats());
    }

    /**
     * Get training data statistics
     */
    @GetMapping("/data/stats")
    public ResponseEntity<Map<String, Object>> getDataStats() {
        return ResponseEntity.ok(trainingService.getDataStats());
    }

    /**
     * Get priors for each category
     */
    @GetMapping("/priors")
    public ResponseEntity<?> getPriors() {
        if (!naiveBayesService.isTrained()) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body(Map.of("error", "Model not trained"));
        }
        return ResponseEntity.ok(naiveBayesService.getCategoryPriors());
    }

    /**
     * Add new training document dynamically
     */
    @PostMapping("/training/add")
    public ResponseEntity<?> addTrainingDocument(
            @RequestParam String category,
            @RequestParam String filename,
            @RequestBody String content) {

        try {
            if (!filename.endsWith(".txt")) {
                filename += ".txt";
            }

            trainingService.addTrainingDocument(category, content, filename);

            return ResponseEntity.ok(Map.of(
                    "message", "Training document added successfully",
                    "category", category,
                    "filename", filename
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to add document",
                            "message", e.getMessage()
                    ));
        }
    }
}
