package com.example.naive_bayes_classifier.controller;


import com.example.naive_bayes_classifier.model.*;
import com.example.naive_bayes_classifier.service.*;
import com.example.naive_bayes_classifier.utils.MetricsCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private NaiveBayesService naiveBayesService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private MetricsCalculator metricsCalculator;

    /**
     * Page d'accueil
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("trained", naiveBayesService.isTrained());
        model.addAttribute("stats", naiveBayesService.getModelStats());
        return "index";
    }

    /**
     * Page d'upload et classification
     */
    @GetMapping("/upload")
    public String uploadPage(Model model) {
        if (!naiveBayesService.isTrained()) {
            model.addAttribute("error", "Le modèle n'est pas entraîné. Veuillez d'abord entraîner le modèle.");
            return "redirect:/";
        }
        model.addAttribute("categories", naiveBayesService.getModelStats().get("categories"));
        return "upload";
    }

    /**
     * Entraîner le modèle
     */
    @PostMapping("/train")
    public String trainModel(Model model) {
        try {
            logger.info("Starting model training...");

            List<TrainingDocument> documents = trainingService.loadTrainingData();

            if (documents.isEmpty()) {
                model.addAttribute("error", "Aucune donnée d'entraînement trouvée dans le dossier Data/");
                model.addAttribute("trained", false);
                return "index";
            }

            naiveBayesService.train(documents);

            model.addAttribute("success", "Modèle entraîné avec succès!");
            model.addAttribute("trained", true);
            model.addAttribute("stats", naiveBayesService.getModelStats());

            logger.info("Training completed successfully");

        } catch (Exception e) {
            logger.error("Training error: {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur lors de l'entraînement: " + e.getMessage());
            model.addAttribute("trained", false);
        }

        return "index";
    }

    /**
     * Entraîner et évaluer le modèle
     */
    @PostMapping("/train-evaluate")
    public String trainAndEvaluate(@RequestParam(defaultValue = "0.2") double testRatio, Model model) {
        try {
            logger.info("Starting training and evaluation with test ratio: {}", testRatio);

            List<TrainingDocument> documents = trainingService.loadTrainingData();

            if (documents.isEmpty()) {
                model.addAttribute("error", "Aucune donnée d'entraînement trouvée");
                return "index";
            }

            // Split data
            Map<String, List<TrainingDocument>> split =
                    trainingService.splitTrainTest(documents, testRatio);

            List<TrainingDocument> trainSet = split.get("train");
            List<TrainingDocument> testSet = split.get("test");

            // Train
            naiveBayesService.train(trainSet);

            // Evaluate
            EvaluationMetrics metrics = metricsCalculator.evaluate(naiveBayesService, testSet);

            model.addAttribute("success", "Entraînement et évaluation terminés!");
            model.addAttribute("trained", true);
            model.addAttribute("trainSize", trainSet.size());
            model.addAttribute("testSize", testSet.size());
            model.addAttribute("metrics", metrics);
            model.addAttribute("stats", naiveBayesService.getModelStats());

            logger.info("Training and evaluation completed successfully");

        } catch (Exception e) {
            logger.error("Training/Evaluation error: {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur: " + e.getMessage());
        }

        return "index";
    }

    /**
     * Classifier un fichier uploadé
     */
    @PostMapping("/classify-file")
    public String classifyFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Veuillez sélectionner un fichier");
                return "upload";
            }

            // Lire le contenu du fichier
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            logger.info("Classifying file: {} ({} chars)", file.getOriginalFilename(), content.length());

            // Classifier
            ClassificationResult result = naiveBayesService.classify(content);

            // Préparer les données pour l'affichage
            model.addAttribute("fileName", file.getOriginalFilename());
            model.addAttribute("content", content);
            model.addAttribute("result", result);
            model.addAttribute("predictedCategory", result.getPredictedCategory());
            model.addAttribute("confidence", String.format("%.2f%%", result.getConfidence() * 100));
            model.addAttribute("probabilities", result.getProbabilities());
            model.addAttribute("totalTokens", result.getTotalTokens());
            model.addAttribute("uniqueTokens", result.getUniqueTokens());

            logger.info("Classification completed: {} (confidence: {:.2f}%)",
                    result.getPredictedCategory(), result.getConfidence() * 100);

            return "result";

        } catch (IOException e) {
            logger.error("File reading error: {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur lors de la lecture du fichier: " + e.getMessage());
            return "upload";
        } catch (Exception e) {
            logger.error("Classification error: {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur lors de la classification: " + e.getMessage());
            return "upload";
        }
    }

    /**
     * Classifier du texte saisi
     */
    @PostMapping("/classify-text")
    public String classifyText(@RequestParam("text") String text, Model model) {
        try {
            if (text == null || text.trim().isEmpty()) {
                model.addAttribute("error", "Le texte ne peut pas être vide");
                return "upload";
            }

            logger.info("Classifying text ({} chars)", text.length());

            // Classifier
            ClassificationResult result = naiveBayesService.classify(text);

            // Préparer les données
            model.addAttribute("fileName", "Texte saisi");
            model.addAttribute("content", text);
            model.addAttribute("result", result);
            model.addAttribute("predictedCategory", result.getPredictedCategory());
            model.addAttribute("confidence", String.format("%.2f%%", result.getConfidence() * 100));
            model.addAttribute("probabilities", result.getProbabilities());
            model.addAttribute("totalTokens", result.getTotalTokens());
            model.addAttribute("uniqueTokens", result.getUniqueTokens());

            return "result";

        } catch (Exception e) {
            logger.error("Classification error: {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur lors de la classification: " + e.getMessage());
            return "upload";
        }
    }
}