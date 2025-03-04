package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageManager;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.Marker;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Node.Node;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class Coverage {
    CoverageGraph coverageGraph;
    CoverageManager coverageManager = new CoverageManager();
    private final MarkVisitor markVisitor = new Marker();

    public Set<Edge> getOutgoingEdges(Node node) {
        return coverageGraph.getOutgoingEdges(node);
    }

    protected boolean isCoveredBy(Edge edge, ResolvedMethodDeclaration method) {
        return coverageManager.isCoveredBy(edge, method);
    }

    protected boolean isCoveredBy(Node node, ResolvedMethodDeclaration method) {
        return coverageManager.isCoveredBy(node, method);
    }

    protected boolean isCoveredBy(Node node, ResolvedFieldDeclaration field) {
        return coverageManager.isCoveredBy(node, field);
    }

    protected boolean isCoveredBy(Edge edge, ResolvedFieldDeclaration field) {
        return coverageManager.isCoveredBy(edge, field);
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
    public CoverageGraph analyze(List<MethodDeclaration> testMethods, Graph graph) {
        Graph filteredGraph = filterGraph(graph);
        this.coverageGraph = new CoverageGraph(filteredGraph);
        return analyzeFilteredGraph(testMethods);
    }

    private CoverageGraph analyzeFilteredGraph(List<MethodDeclaration> testMethods) {
        // Iterate over each CompilationUnit and analyze test method relationships
        testMethods.stream()
                /*.filter(e -> !e.isPrivate()) */
                .forEach(testMethod -> analyzeTestMethod(testMethod, testMethods));
        analyzeRemainingGraph();
        return coverageGraph;
    }

    /**
     * Analyzes a single test method for coverage
     */
    protected abstract void analyzeTestMethod(MethodDeclaration testMethod, List<MethodDeclaration> testMethods);

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


    protected Optional<MethodDeclaration> getTestMethod(ResolvedMethodDeclaration methodDeclaration, List<MethodDeclaration> testMethods) {
        for (MethodDeclaration testMethod : testMethods) {
            if (testMethod.resolve().getQualifiedSignature().equals(methodDeclaration.getQualifiedSignature())) {
                return Optional.of(testMethod);
            }
        }
        return Optional.empty();
    }


}
