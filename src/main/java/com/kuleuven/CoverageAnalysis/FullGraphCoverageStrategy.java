package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.model.SymbolReference;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.MethodCallEdge;
import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;

public class FullGraphCoverageStrategy extends CoverageTemplate {
    @Override
    protected void filterNodes(Graph newGraph, Graph graph) {
        // No filtering necessary, as we are analyzing the entire graph
    }

    @Override
    protected void filterEdges(Graph newGraph, Graph graph) {
        // No filtering necessary, as we are analyzing the entire graph
    }

    /**
     * Analyzes a single resolved method call for coverage relationships between nodes and edges
     */
    @Override
    protected void analyzeMethodCall(ResolvedMethodDeclaration resolvedTestMethod) {
        /*
        coverageGraph.getEdges()
                .forEach(untestedEdge -> analyzeEdge(resolvedTestMethod, untestedEdge));

        coverageGraph.getNodes()
                .forEach(untestedNode -> analyzeNode(resolvedTestMethod, untestedNode));

         */
    }
    /*
    /**
     * Analyzes a method node to determine if it matches the given method call name
     * and marks the node in the coverage graph if they match.
     *
     * @param testMethod   The resolved method declaration.
     * @param untestedNode The method node representing a method in the graph to be checked and marked for coverage.
     *
    private void analyzeNode(ResolvedMethodDeclaration testMethod, Node untestedNode) {
        if (untestedNode.isCoveredBy(testMethod)) {
            coverageGraph.markNode(untestedNode);
        }
    }

    /**
     * Analyzes an edge to detect coverage relationships.
     *
    private void analyzeEdge(ResolvedMethodDeclaration testMethod, Edge untestedEdge) {
        MethodNode untestedDest = (MethodNode) coverageGraph.getNode(untestedEdge.getDestination());

        // Check if the test method directly matches this method node
        if (testMethodCallName.equals(untestedDest.getName())) {
            coverageGraph.markNode(untestedDest);
            analyzeClassCoverage(testedClass, untestedDest, untestedEdge, coverageGraph);
        }
    }

    /**
     * Analyzes the class relationship (type resolution) for coverage between a test class and a tested class.
     *
    private void analyzeClassCoverage(ResolvedReferenceTypeDeclaration testedClass, MethodNode untestedDest,
                                      MethodCallEdge untestedEdge, CoverageGraph coverageGraph) {
        String untestedClassName = untestedDest.getClassName();
        SymbolReference<ResolvedReferenceTypeDeclaration> untestedClassRef = solver.tryToSolveType(untestedClassName);

        if (untestedClassRef.isSolved()) {
            ResolvedReferenceTypeDeclaration untestedClass = untestedClassRef.getCorrespondingDeclaration();
            if (testedClass.isAssignableBy(untestedClass) || untestedClass.isAssignableBy(testedClass)) {
                coverageGraph.markEdge(untestedEdge);
            }
        }
    }
    */
}
