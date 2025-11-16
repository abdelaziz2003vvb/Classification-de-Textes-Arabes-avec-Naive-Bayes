package com.example.naive_bayes_classifier.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import safar.util.tokenization.impl.SAFARTokenizer;
import safar.util.tokenization.interfaces.ITokenizer;
import safar.basic.morphology.stemmer.factory.StemmerFactory;
import safar.basic.morphology.stemmer.interfaces.IStemmer;
import safar.basic.morphology.stemmer.model.WordStemmerAnalysis;

import java.util.*;

@Service
public class TextPreprocessingService {

    private static final Logger logger = LoggerFactory.getLogger(TextPreprocessingService.class);

    private final ITokenizer tokenizer;
    private final IStemmer stemmer;
    private final StopWordsService stopWordsService;

    public TextPreprocessingService(StopWordsService stopWordsService) throws Exception {
        this.tokenizer = new SAFARTokenizer();
        this.stemmer = StemmerFactory.getImplementation("ISRI_STEMMER");
        this.stopWordsService = stopWordsService;
        logger.info("SAFAR Text Preprocessing Service initialized successfully");
    }

    /**
     * Tokenize Arabic text using SAFAR
     */
    public List<String> tokenize(String text) {
        try {
            String[] tokens = tokenizer.tokenize(text);
            return Arrays.asList(tokens);
        } catch (Exception e) {
            logger.error("Error during tokenization: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Stem tokens using SAFAR ISRI Stemmer
     */
    public List<String> stem(List<String> tokens) {
        List<String> stems = new ArrayList<>();

        for (String token : tokens) {
            // Skip stop words
            if (stopWordsService.isStopWord(token)) {
                logger.debug("Skipping stop word: {}", token);
                continue;
            }

            try {
                List<WordStemmerAnalysis> analyses = stemmer.stem(token);

                if (analyses != null && !analyses.isEmpty()
                        && !analyses.get(0).getListStemmerAnalysis().isEmpty()) {
                    String stem = analyses.get(0).getListStemmerAnalysis().get(0).getMorpheme();
                    stems.add(stem);
                } else {
                    stems.add(token);
                }
            } catch (Exception e) {
                logger.warn("Stemming failed for token '{}': {}", token, e.getMessage());
                stems.add(token);
            }
        }

        return stems;
    }

    /**
     * Complete preprocessing pipeline: tokenize + stem + filter stop words
     */
    public List<String> preprocess(String text) {
        logger.debug("Preprocessing text: {}", text.substring(0, Math.min(50, text.length())));

        // Step 1: Tokenization
        List<String> tokens = tokenize(text);
        logger.debug("Tokenization: {} tokens", tokens.size());

        // Step 2: Stemming (stop words are filtered here)
        List<String> stems = stem(tokens);
        logger.debug("After stemming and stop word removal: {} stems", stems.size());

        return stems;
    }

    /**
     * Calculate term frequency
     */
    public Map<String, Integer> calculateTermFrequency(List<String> stems) {
        Map<String, Integer> termFreq = new HashMap<>();
        for (String stem : stems) {
            termFreq.put(stem, termFreq.getOrDefault(stem, 0) + 1);
        }
        return termFreq;
    }

    /**
     * Get preprocessing statistics
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("tokenizerType", "SAFAR Tokenizer");
        stats.put("stemmerType", "ISRI Stemmer");
        stats.put("stopWordsCount", stopWordsService.getStopWordsCount());
        return stats;
    }
}