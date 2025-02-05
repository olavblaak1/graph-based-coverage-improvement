package com.kuleuven.GraphExtraction;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.kuleuven.Graph.Graph;
import com.kuleuven.GraphExtraction.ExtractionStrategy.Template.*;
import com.kuleuven.GraphExtraction.ExtractionStrategy.ExtractionStrategy;

import java.util.List;

public class GraphExtractor {

    private Graph graph;

    /*
     * Extraction strategy to use for extracting the graph from the Java source file
     * This dictates whether the nodes are class definitions, method definitions, ...
     * and if the edges are method calls, inheritence relations, ...
     */
    private ExtractionTemplate<? extends Node> extractionTemplate;

    public GraphExtractor(ExtractionStrategy strategy) {
        switch (strategy) {
            case ORIGINAL:
                extractionTemplate = new ExtractGraphOriginal();
                break;
            case INHERITANCE_FIELDS:
                extractionTemplate = new ExtractGraphInheritanceFields();
                break;
            case METHODS_CALLS:
                extractionTemplate = new ExtractMethodGraph();
                break;
            case FULL_GRAPH:
                extractionTemplate = new ExtractFullGraph();
                break;
            default:
                extractionTemplate = new ExtractGraphOriginal();
        }
        this.graph = new Graph();
    }


    public void extractGraph(List<CompilationUnit> compilationUnits) {
        extractionTemplate.extractGraph(compilationUnits, graph);
    }

    Graph getGraph() {
        return graph;
    }
}