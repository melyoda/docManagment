package com.mawkszuxz.docmanagment.util;

import lombok.Getter;

@Getter
public class CategoryResult {
    private final String mainCategory;
    private final String subCategory;

    public CategoryResult(String mainCategory, String subCategory) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
    }

    // A handy factory for uncategorized documents
    public static CategoryResult uncategorized() {
        return new CategoryResult("Uncategorized", null);
    }
}