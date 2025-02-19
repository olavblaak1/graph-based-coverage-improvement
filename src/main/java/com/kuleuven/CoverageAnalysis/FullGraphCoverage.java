package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodAmbiguityException;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.Graph.Graph;

public class FullGraphCoverage extends Coverage {
    @Override
    protected void analyzeTestMethod(MethodDeclaration testMethod) {
        // Collect all method calls within the test method
        testMethod.findAll(MethodCallExpr.class).forEach(testCall -> {
            try {
                ResolvedMethodDeclaration resolvedTestMethod = testCall.resolve();
                analyzeMethodCall(resolvedTestMethod);
            } catch (UnsolvedSymbolException | IllegalArgumentException | MethodAmbiguityException e) {
                System.err.println("Warning: Unsolved or invalid symbol during test method analysis - " + e.getMessage());
            }
        });
        testMethod.findAll(FieldAccessExpr.class).forEach(testCall -> {
            try {
                if (testCall.resolve().isField()) {
                    ResolvedFieldDeclaration resolvedFieldDeclaration = (ResolvedFieldDeclaration) testCall.resolve();
                    analyzeFieldAccess(resolvedFieldDeclaration);
                }
            } catch (UnsolvedSymbolException | IllegalArgumentException | MethodAmbiguityException e) {
                System.err.println(e.getMessage());
            }
        });
    }


    @Override
    protected void filterNodes(Graph newGraph, Graph graph) {
        // No filtering necessary, as we are analyzing the entire graph
    }

    @Override
    protected void filterEdges(Graph newGraph, Graph graph) {
        // TODO: Choose what edges to keep for coverage
    }

    @Override
    protected void analyzeMethodCall(ResolvedMethodDeclaration resolvedTestMethod) {
        coverageGraph.getNodes()
                .forEach(untestedNode -> {
                    if (isCoveredBy(untestedNode, resolvedTestMethod)) {
                        markNode(untestedNode);
                    }
                });

        coverageGraph.getEdges()
                .forEach(untestedEdge -> {
                    if (isCoveredBy(untestedEdge, resolvedTestMethod)) {
                        markEdge(untestedEdge);
                    }
                });
    }


    private void analyzeFieldAccess(ResolvedFieldDeclaration resolvedFieldDeclaration) {
        coverageGraph.getNodes()
                .forEach(untestedNode -> {
                    if (isCoveredBy(untestedNode, resolvedFieldDeclaration)) {
                        markNode(untestedNode);
                    }
                });


        coverageGraph.getEdges()
                .forEach(untestedEdge -> {
                    if (isCoveredBy(untestedEdge, resolvedFieldDeclaration)) {
                        markEdge(untestedEdge);
                    }
                });
    }

    @Override
    protected void analyzeRemainingGraph() {
        // TODO: Technically, any information gathered from such a step would be duplicate work, as it is just derived
        // from the previous information.
    }
}
