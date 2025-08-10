package com.mawkszuxz.docmanagment.util;

import java.util.List;
import lombok.Getter;

@Getter
public class ClassificationScore {
    private final List<String> path;
    private final double score;

    public ClassificationScore(List<String> path, double score) {
        this.path = path;
        this.score = score;
    }
}