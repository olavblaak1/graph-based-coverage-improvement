package com.kuleuven.MissingTestIdentification.SubGraphExtraction.ExtractionAlgorithms;

public class CoverageRange {
    private final double start;
    private final double end;

    public CoverageRange(double start, double end) {
        if (start < 0 || end > 1 || start > end) {
            throw new IllegalArgumentException("Coverage range must be between 0 and 1, and start must be less than or equal to end.");
        }
        this.start = start;
        this.end = end;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }
}
