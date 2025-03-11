package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodAmbiguityException;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.NodeType;

import java.util.List;

public class MethodGraphCoverage extends Coverage {


    @Override
    protected void analyzeTestMethod(MethodDeclaration testMethod, List<MethodDeclaration> testMethods) {
        // Collect all method calls within the test method
        testMethod.findAll(MethodCallExpr.class).forEach(testCall -> {
            try {
                ResolvedMethodDeclaration resolvedTestMethod = testCall.resolve();
                getTestMethod(resolvedTestMethod, testMethods).ifPresentOrElse(
                        e -> analyzeTestMethod(e, testMethods),
                        () -> analyzeMethodCall(resolvedTestMethod));
                analyzeMethodCall(resolvedTestMethod);
            } catch (UnsolvedSymbolException | IllegalArgumentException | MethodAmbiguityException e) {
                System.err.println("Warning: Unsolved or invalid symbol during test method analysis - " + e.getMessage());
            }
        });
    }

    @Override
    public void filterNodes(Graph newGraph, Graph graph) {
        // Retain only MethodNodes
        graph.getNodes().stream()
                .filter(node -> !(node instanceof MethodNode))
                .forEach(newGraph::removeNode);
    }

    @Override
    public void filterEdges(Graph newGraph, Graph graph) {
        // Retain only MethodCallEdges where both endpoints are MethodNodes
        graph.getEdges().stream()
                .filter(edge -> (!(edge.getType().equals(EdgeType.METHOD_CALL)) &&
                        !(edge.getType().equals(EdgeType.OVERRIDES))) ||
                        !(edge.getSource().getType().equals(NodeType.METHOD)) ||
                        !(edge.getDestination().getType().equals(NodeType.METHOD)))
                .forEach(newGraph::removeEdge);
    }

    /**
     * Analyzes a single resolved method call for coverage relationships between nodes and edges
     */
    @Override
    protected void analyzeMethodCall(ResolvedMethodDeclaration resolvedTestMethod) {
        coverageGraph.getNodes()
                .forEach(untestedNode -> analyzeNode(resolvedTestMethod, untestedNode));
    }

    @Override
    protected void analyzeRemainingGraph() {
        // TODO: Technically, any information gathered from such a step would be duplicate work, as it is just derived
        // from the previous information.
    }

    /**
     * Analyzes a method node to determine if it matches the given method call name
     * and marks the node in the coverage graph if they match.
     *
     * @param methodDeclaration The fully qualified name of the test method call being analyzed.
     * @param untestedNode      The method node representing a method in the graph to be checked and marked for coverage.
     */
    private void analyzeNode(ResolvedMethodDeclaration methodDeclaration, Node untestedNode) {
        if (isCoveredBy(untestedNode, methodDeclaration)) {
            markNode(untestedNode);
        }
    }
}