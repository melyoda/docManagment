package com.mawkszuxz.docmanagment.util;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map; // Import Map

@Getter
@Setter
public class CategoryNode {
    private String name;
    // Changed from List<String> to Map<String, Double>
    private Map<String, Double> weightedKeywords;
    private List<CategoryNode> subcategories;

    public CategoryNode(String name, Map<String, Double> weightedKeywords, List<CategoryNode> subcategories) {
        this.name = name;
        this.weightedKeywords = weightedKeywords;
        this.subcategories = subcategories;
    }
}