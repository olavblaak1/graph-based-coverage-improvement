package com.kuleuven.GraphExtraction.Extraction;

import com.github.javaparser.ast.CompilationUnit;

import com.kuleuven.GraphExtraction.Extraction.ExtractionStrategy.ExtractMethodGraph;
import com.kuleuven.GraphExtraction.Extraction.ExtractionStrategy.ExtractGraphInheritanceFields;
import com.kuleuven.GraphExtraction.Extraction.ExtractionStrategy.ExtractGraphOriginal;
import com.kuleuven.GraphExtraction.Extraction.ExtractionStrategy.ExtractionStrategy;
import com.kuleuven.GraphExtraction.Extraction.ExtractionStrategy.ExtractionTemplate;
import com.kuleuven.GraphExtraction.Graph.Node;
import com.kuleuven.GraphExtraction.Graph.Edge.Edge;
import java.util.LinkedList;
import java.util.List;

public class GraphExtractor {

    private List<Node> nodes;
    private List<Edge> edges;

    /*
     * Extraction strategy to use for extracting the graph from the Java source file
     * This dictates whether the nodes are class definitions, method definitions, ...
     * and if the edges are method calls, inheritence relations, ...
     */
    private ExtractionTemplate extractionTemplate;

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
            default:
                extractionTemplate = new ExtractGraphOriginal();
        }
        nodes = new LinkedList<>();
        edges = new LinkedList<>();
    }


    public void extractGraph(List<CompilationUnit> compilationUnits) {
        extractionTemplate.extractGraph(compilationUnits, edges, nodes);
    }


    public List<Node> getNodes() {
        return new LinkedList<>(nodes);
    }

    public List<Edge> getEdges() {
        return new LinkedList<>(edges);
    }
}