package com.kuleuven.GraphExtraction.ExtractionStrategy;

public enum ExtractionStrategy {

    // LEVEL 1: CLASSES
    ORIGINAL, // Original Graph Extraction method from Charles Sys' Thesis
    INHERITANCE_FIELDS, // Also extract inheritance and fields

    // LEVEL 2: METHODS
    METHODS_CALLS 
}
