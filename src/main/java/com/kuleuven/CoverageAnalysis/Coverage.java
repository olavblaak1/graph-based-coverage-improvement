package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.FieldCoverageChecker;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.MethodCoverageChecker;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.Marker;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Node.Node;

import java.util.List;
import java.util.Set;

public abstract class Coverage {
    CoverageGraph coverageGraph;
    private CoverageVisitor<ResolvedMethodDeclaration> methodCoverageVisitor = new MethodCoverageChecker();
    private CoverageVisitor<ResolvedFieldDeclaration> fieldCoverageVisitor = new FieldCoverageChecker();
    private MarkVisitor markVisitor = new Marker();

    public Set<Edge> getOutgoingEdges(Node node) {
        return coverageGraph.getOutgoingEdges(node);
    }

    public boolean isCoveredBy(Edge edge, ResolvedMethodDeclaration method) {
        return edge.accept(methodCoverageVisitor, method);
    }

    public boolean isCoveredBy(Node node, ResolvedMethodDeclaration method) {
        return node.accept(methodCoverageVisitor, method);
    }

    public boolean isCoveredBy(Node node, ResolvedFieldDeclaration field) {
        return node.accept(fieldCoverageVisitor, field);
    }

    public boolean isCoveredBy(Edge edge, ResolvedFieldDeclaration field) {
        return edge.accept(fieldCoverageVisitor, field);
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
        analyzeRemainingGraph();
        return coverageGraph;
    }

    /**
     * Analyzes a single test method for coverage
     */
    protected abstract void analyzeTestMethod(MethodDeclaration testMethod);

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

    protected abstract void analyzeRemainingGraph();

}
