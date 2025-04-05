package com.kuleuven.TestMinimization;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodAmbiguityException;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageManager;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.TestMinimization.ImportanceCalculation.GraphImportanceVisitor;

import java.util.*;

public class Minimizer {

    GraphImportanceVisitor graphImportanceVisitor;
    CoverageManager coverageManager;

    public Minimizer(GraphImportanceVisitor graphImportanceVisitor) {
        this.graphImportanceVisitor = graphImportanceVisitor;
        this.coverageManager = new CoverageManager();
    }


    public Map<MethodDeclaration, Double> minimizeTests(RankedGraph<CoverageGraph> graph, Collection<MethodDeclaration> testMethods) {
        Map<MethodDeclaration, Double> minimizedTests = new HashMap<>();
        testMethods.forEach(methodDeclaration ->
                minimizedTests.put(methodDeclaration, analyzeTestMethod(methodDeclaration, graph, testMethods)));
        return minimizedTests;
    }

    private Optional<MethodDeclaration> getTestMethod(ResolvedMethodDeclaration methodDeclaration, Collection<MethodDeclaration> testMethods) {
        for (MethodDeclaration testMethod : testMethods) {
            if (testMethod.resolve().getQualifiedSignature().equals(methodDeclaration.getQualifiedSignature())) {
                return Optional.of(testMethod);
            }
        }
        return Optional.empty();
    }

    private double analyzeTestMethod(MethodDeclaration methodDeclaration, RankedGraph<CoverageGraph> graph, Collection<MethodDeclaration> testMethods) {
        double testMethodImportance = 0.0;
        for (MethodCallExpr methodCallExpr : methodDeclaration.findAll(MethodCallExpr.class)) {
            try {
                ResolvedMethodDeclaration calledMethod = methodCallExpr.resolve();
                // We need to check if the called method is a test method, because if so,
                // we need to also analyze the effects of that test method that is called,
                // otherwise structural methods (methods in a test suite that only call other test methods) have an importance of 0.
                Optional<MethodDeclaration> correspondingTestMethod = getTestMethod(calledMethod, testMethods);
                if (correspondingTestMethod.isPresent()) {
                    testMethodImportance += analyzeTestMethod(correspondingTestMethod.get(), graph, testMethods);
                } else {
                    testMethodImportance += calculateMethodCallImportance(graph, calledMethod);
                }
            } catch (UnsolvedSymbolException e) {
                System.err.println("Warning: Unsolved or invalid symbol during test method analysis - " + e.getMessage());
            } catch (MethodAmbiguityException e) {
                System.err.println("Warning: Ambiguous method call during test method analysis - " + e.getMessage());
            }
        }

        for (FieldAccessExpr fieldAccessExpr : methodDeclaration.findAll(FieldAccessExpr.class)) {
            try {
                if (fieldAccessExpr.resolve().isField()) {
                    ResolvedFieldDeclaration fieldDeclaration = fieldAccessExpr.resolve().asField();
                    testMethodImportance += calculateFieldAccessImportance(graph, fieldDeclaration);
                }
            } catch (UnsolvedSymbolException e) {
                System.err.println("Warning: Unsolved or invalid symbol during test method analysis - " + e.getMessage());
            } catch (MethodAmbiguityException e) {
                System.err.println("Warning: Ambiguous field access during test method analysis - " + e.getMessage());
            }
        }
        return testMethodImportance;
    }

    private double calculateFieldAccessImportance(RankedGraph<CoverageGraph> graph, ResolvedFieldDeclaration fieldDeclaration) {
        double fieldAccessImportance = 0.0;
        for (Node node : graph.getNodes()) {
            if (coverageManager.isCoveredBy(node, fieldDeclaration)) {
                fieldAccessImportance += node.accept(graphImportanceVisitor, graph);
            }
        }
        for (Edge edge : graph.getEdges()) {
            if (coverageManager.isCoveredBy(edge, fieldDeclaration)) {
                fieldAccessImportance += edge.accept(graphImportanceVisitor, graph);
            }
        }
        return fieldAccessImportance;
    }

    private double calculateMethodCallImportance(RankedGraph<CoverageGraph> graph, ResolvedMethodDeclaration calledMethod) {
        double methodCallImportance = 0.0;
        for (Node node : graph.getNodes()) {
            if (coverageManager.isCoveredBy(node, calledMethod)) {
                methodCallImportance += node.accept(graphImportanceVisitor, graph);
            }
        }
        for (Edge edge : graph.getEdges()) {
            if (coverageManager.isCoveredBy(edge, calledMethod)) {
                methodCallImportance += edge.accept(graphImportanceVisitor, graph);
            }
        }
        return methodCallImportance;
    }
}