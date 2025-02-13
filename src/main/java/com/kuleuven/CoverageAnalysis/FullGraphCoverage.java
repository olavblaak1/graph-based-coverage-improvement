package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.NodeType;

public class FullGraphCoverage extends Coverage {
    @Override
    protected void filterNodes(Graph newGraph, Graph graph) {
        // No filtering necessary, as we are analyzing the entire graph
    }

    @Override
    protected void filterEdges(Graph newGraph, Graph graph) {
        graph.getEdges().stream()
                .filter(edge -> (edge.getType().equals(EdgeType.FIELD))) // TEMPORARILY DO NOT LOOK AT FIELDS
                .forEach(newGraph::removeEdge);
    }

    /**
     * Analyzes a single resolved method call for coverage relationships between nodes and edges
     */
    @Override
    protected void analyzeMethodCall(ResolvedMethodDeclaration resolvedTestMethod) {
        coverageGraph.getNodes()
                .forEach(untestedNode -> analyzeNode(resolvedTestMethod, untestedNode));

        coverageGraph.getEdges()
                .forEach(untestedEdge -> analyzeEdge(resolvedTestMethod, untestedEdge));
    }

    /**
     * Analyzes a method node to determine if it matches the given method call name
     * and marks the node in the coverage graph if they match.
     *
     * @param testMethod   The resolved method declaration.
     * @param untestedNode The method node representing a method in the graph to be checked and marked for coverage.
     **/
    private void analyzeNode(ResolvedMethodDeclaration testMethod, Node untestedNode) {
        if (isCoveredBy(untestedNode, testMethod)) {
            markNode(untestedNode);
        }
    }

    /**
     * Analyzes an edge to detect coverage relationships.
     **/
    private void analyzeEdge(ResolvedMethodDeclaration testMethod, Edge untestedEdge) {
        if (isCoveredBy(untestedEdge, testMethod)) {
            markEdge(untestedEdge);
        }
    }
}
