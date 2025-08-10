package com.mawkszuxz.docmanagment.service;

import com.mawkszuxz.docmanagment.util.CategoryNode;
import com.mawkszuxz.docmanagment.util.CategoryResult;
import com.mawkszuxz.docmanagment.util.ClassificationScore;
import com.mawkszuxz.docmanagment.util.ClassificationTree;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CategorizationService {

    private final CategoryNode root = ClassificationTree.buildTree();

    public CategoryResult classify(String text) {
        if (text == null || text.isBlank()) {
            return CategoryResult.uncategorized();
        }

        // --- STEP 1: TOKENIZE AND CREATE FREQUENCY MAP (ONCE) ---
        // Splits by whitespace or punctuation, creating a clean list of words.
        List<String> tokens = Arrays.asList(text.toLowerCase().split("[\\s\\p{Punct}]+"));
        Map<String, Long> frequencyMap = tokens.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // --- FIND BEST SCORE USING THE MAP ---
        ClassificationScore bestMatch = findBestScore(root, frequencyMap);

        if (bestMatch.getScore() == 0) {
            return CategoryResult.uncategorized();
        }

        // --- PROCESS THE RESULT (No changes here) ---
        List<String> path = bestMatch.getPath();
        if (!path.isEmpty() && "Root".equals(path.get(0))) {
            path.remove(0);
        }

        if (path.isEmpty()) {
            return CategoryResult.uncategorized();
        } else if (path.size() == 1) {
            return new CategoryResult(path.get(0), null);
        } else {
            return new CategoryResult(path.get(path.size() - 2), path.get(path.size() - 1));
        }
    }

    /**
     * Recursively traverses the tree using a pre-computed frequency map for high performance.
     */
    private ClassificationScore findBestScore(CategoryNode node, Map<String, Long> frequencyMap) {
        // --- CORE LOGIC CHANGE: LOOKUP INSTEAD OF SEARCH ---
        double currentScore = 0;
        for (var entry : node.getWeightedKeywords().entrySet()) {
            String keyword = entry.getKey();
            double weight = entry.getValue();

            // Fast lookup in the map. getOrDefault is safe and clean.
            long occurrences = frequencyMap.getOrDefault(keyword, 0L);

            if (occurrences > 0) {
                currentScore += occurrences * weight;
            }
        }

        ClassificationScore bestSubcategoryScore = new ClassificationScore(Collections.emptyList(), 0.0);

        // Recursion logic remains the same
        for (CategoryNode child : node.getSubcategories()) {
            ClassificationScore childScore = findBestScore(child, frequencyMap);
            if (childScore.getScore() > bestSubcategoryScore.getScore()) {
                bestSubcategoryScore = childScore;
            }
        }

        if (bestSubcategoryScore.getScore() > currentScore && bestSubcategoryScore.getScore() > 0) {
            List<String> fullPath = new ArrayList<>();
            fullPath.add(node.getName());
            fullPath.addAll(bestSubcategoryScore.getPath());
            return new ClassificationScore(fullPath, bestSubcategoryScore.getScore());
        } else {
            List<String> path = new ArrayList<>();
            path.add(node.getName());
            return new ClassificationScore(path, currentScore);
        }
    }
}