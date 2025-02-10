package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Edge.MethodCallEdge;
import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;

public class MethodCoverageStrategy extends CoverageTemplate {


    @Override
    public void filterNodes(Graph newGraph, Graph graph) {
        // Retain only MethodNodes
        graph.getNodes().stream()
                .filter(node -> !(node instanceof MethodNode))
                .map(Node::getName)
                .forEach(newGraph::removeNode);
    }

    @Override
    public void filterEdges(Graph newGraph, Graph graph) {
        // Retain only MethodCallEdges where both endpoints are MethodNodes
        graph.getEdges().stream()
                .filter(edge -> !(edge instanceof MethodCallEdge) ||
                        !(newGraph.getNode(edge.getSource()) instanceof MethodNode) ||
                        !(newGraph.getNode(edge.getDestination()) instanceof MethodNode))
                .forEach(newGraph::removeEdge);
    }

    /**
     * Analyzes a single resolved method call for coverage relationships between nodes and edges
     */
    @Override
    protected void analyzeMethodCall(ResolvedMethodDeclaration resolvedTestMethod) {
        String testMethodCallName = resolvedTestMethod.getQualifiedName();
        ResolvedReferenceTypeDeclaration testedClass = resolvedTestMethod.declaringType().asReferenceType();
        coverageGraph.getEdges().stream()
                .map(edge -> (MethodCallEdge) edge)
                .forEach(untestedEdge -> analyzeEdge(testMethodCallName, testedClass, untestedEdge));

        coverageGraph.getNodes().stream()
                .map(node -> (MethodNode) node)
                .forEach(untestedNode -> analyzeNode(testMethodCallName, untestedNode));
    }

    /**
     * Analyzes a method node to determine if it matches the given method call name
     * and marks the node in the coverage graph if they match.
     *
     * @param testMethodCallName The fully qualified name of the test method call being analyzed.
     * @param untestedNode       The method node representing a method in the graph to be checked and marked for coverage.
     */
    private void analyzeNode(String testMethodCallName, MethodNode untestedNode) {
        if (testMethodCallName.equals(untestedNode.getName())) {
            coverageGraph.markNode(untestedNode);
        }
    }

    /**
     * Analyzes an edge to detect coverage relationships.
     */
    private void analyzeEdge(String testMethodCallName, ResolvedReferenceTypeDeclaration testedClass,
                             MethodCallEdge untestedEdge) {
        MethodNode untestedDest = (MethodNode) coverageGraph.getNode(untestedEdge.getDestination());

        // Check if the test method directly matches this method node
        if (testMethodCallName.equals(untestedDest.getName())) {
            coverageGraph.markNode(untestedDest);
            analyzeClassCoverage(testedClass, untestedDest, untestedEdge, coverageGraph);
        }
    }

    /**
     * Analyzes the class relationship (type resolution) for coverage between a test class and a tested class.
     */
    private void analyzeClassCoverage(ResolvedReferenceTypeDeclaration testedClass, MethodNode untestedDest,
                                      MethodCallEdge untestedEdge, CoverageGraph coverageGraph) {
        String untestedClassName = untestedDest.getClassName();
        if (untestedClassName.equals(testedClass.getQualifiedName())) {
            coverageGraph.markEdge(untestedEdge);
        }
    }
}