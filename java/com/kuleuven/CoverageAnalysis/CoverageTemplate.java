package com.kuleuven.CoverageAnalysis;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.resolution.TypeSolver;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph;

public abstract class CoverageTemplate {
    CoverageGraph coverageGraph;
    TypeSolver solver;

    /*
     * Analysis happens in a few steps:
     * first the generic graph is filtered only to contain the relevant nodes and (possibly) edges
     * then the graph is analyzed to determine which relevant nodes and edges are covered
     */
    public CoverageGraph analyze(List<CompilationUnit> cus, Graph graph, TypeSolver solver) {
        Graph filteredGraph = filterGraph(graph);
        this.coverageGraph = new CoverageGraph(filteredGraph);
        this.solver = solver;
        return analyzeFilteredGraph(cus);
    }
    
    public Graph filterGraph(Graph graph) {
        Graph newGraph = new Graph();
        filterNodes(newGraph, graph);
        filterEdges(newGraph, graph);
        return newGraph;
    }

    // These methods are implemented by the concrete strategy
    abstract void filterNodes(Graph newGraph, Graph graph);
    abstract void filterEdges(Graph newGraph, Graph graph);
    abstract CoverageGraph analyzeFilteredGraph(List<CompilationUnit> cus);
}
