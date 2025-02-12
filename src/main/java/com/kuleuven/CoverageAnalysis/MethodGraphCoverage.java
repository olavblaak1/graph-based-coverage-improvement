package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.MethodCallEdge;
import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class MethodGraphCoverage extends Coverage {


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
                .filter(edge -> !(edge instanceof MethodCallEdge) ||
                        !(edge.getSource() instanceof MethodNode) ||
                        !(edge.getDestination() instanceof MethodNode))
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

    /**
     * Analyzes a method node to determine if it matches the given method call name
     * and marks the node in the coverage graph if they match.
     *
     * @param methodDeclaration The fully qualified name of the test method call being analyzed.
     * @param untestedNode      The method node representing a method in the graph to be checked and marked for coverage.
     */
    private void analyzeNode(ResolvedMethodDeclaration methodDeclaration, Node untestedNode) {
        if (untestedNode.accept(coverageVisitor, methodDeclaration)) {
            markNode(untestedNode);
        }
    }
}