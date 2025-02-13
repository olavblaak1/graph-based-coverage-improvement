package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodAmbiguityException;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageChecker;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.Marker;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.Node.Node;

import java.util.List;
import java.util.Set;

public abstract class Coverage {
    CoverageGraph coverageGraph;
    private CoverageVisitor coverageVisitor = new CoverageChecker();
    private MarkVisitor markVisitor = new Marker();

    public Set<Edge> getOutgoingEdges(Node node) {
        return coverageGraph.getOutgoingEdges(node);
    }

    public boolean isCoveredBy(Edge edge, ResolvedMethodDeclaration method) {
        return edge.accept(coverageVisitor, method);
    }

    public boolean isCoveredBy(Node node, ResolvedMethodDeclaration method) {
        return node.accept(coverageVisitor, method);
    }

    public void markEdge(Edge edge) {
        edge.accept(markVisitor, coverageGraph);
    }

    public void markNode(Node node) {
        node.accept(markVisitor, coverageGraph);
    }

    /*
     * Analysis happens in a few steps:
     * first the generic graph is filtered only to contain the relevant nodes and (possibly) edges
     * then the graph is analyzed to determine which relevant nodes and edges are covered
     */
    public CoverageGraph analyze(List<CompilationUnit> cus, Graph graph) {
        Graph filteredGraph = filterGraph(graph);
        this.coverageGraph = new CoverageGraph(filteredGraph);
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

    protected abstract void analyzeMethodCall(ResolvedMethodDeclaration testMethod);

}
