package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodAmbiguityException;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph;

import java.util.List;

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

    private CoverageGraph analyzeFilteredGraph(List<CompilationUnit> cus) {
        // Iterate over each CompilationUnit and analyze test method relationships
        cus.forEach(cu -> cu.findAll(MethodDeclaration.class).forEach(this::analyzeTestMethod));
        return coverageGraph;
    }

    /**
     * Analyzes a single test method for coverage
     */
    private void analyzeTestMethod(MethodDeclaration testMethod) {
        // Collect all method calls within the test method
        testMethod.findAll(MethodCallExpr.class).forEach(testCall -> {
            try {
                ResolvedMethodDeclaration resolvedTestMethod = testCall.resolve();
                analyzeMethodCall(resolvedTestMethod);
            } catch (UnsolvedSymbolException | IllegalArgumentException | MethodAmbiguityException e) {
                System.err.println("Warning: Unsolved or invalid symbol during test method analysis - " + e.getMessage());
            }
        });
    }

    public Graph filterGraph(Graph graph) {
        Graph newGraph = new Graph(graph);
        filterNodes(newGraph, graph);
        filterEdges(newGraph, graph);
        return newGraph;
    }

    // These methods are implemented by the concrete strategy
    protected abstract void filterNodes(Graph newGraph, Graph graph);

    protected abstract void filterEdges(Graph newGraph, Graph graph);

    protected abstract void analyzeMethodCall(ResolvedMethodDeclaration resolvedTestMethod);

}
