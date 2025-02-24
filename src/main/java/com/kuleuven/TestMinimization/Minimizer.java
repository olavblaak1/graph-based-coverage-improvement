package com.kuleuven.TestMinimization;

import com.github.javaparser.ast.CompilationUnit;
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
import javassist.expr.MethodCall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Minimizer {

    TestCaseVisitor testCaseVisitor;
    CoverageManager coverageManager;

    public Minimizer(TestCaseVisitor testCaseVisitor) {
        this.testCaseVisitor = testCaseVisitor;
        this.coverageManager = new CoverageManager();
    }


    public Map<MethodDeclaration, Double> minimizeTests(RankedGraph<CoverageGraph> graph, List<MethodDeclaration> testClasses) {
        Map<MethodDeclaration, Double> minimizedTests = new HashMap<>();
        testClasses.forEach(methodDeclaration ->
                                minimizedTests.put(methodDeclaration, analyzeTestMethod(methodDeclaration, graph)));
        return minimizedTests;
    }

    private double analyzeTestMethod(MethodDeclaration methodDeclaration, RankedGraph<CoverageGraph> graph) {
        double testMethodImportance = 0.0;
        for (MethodCallExpr methodCallExpr : methodDeclaration.findAll(MethodCallExpr.class)) {
            try {
                ResolvedMethodDeclaration calledMethod = methodCallExpr.resolve();
                testMethodImportance += calculateMethodCallImportance(graph, calledMethod);
            }
            catch (UnsolvedSymbolException e) {
                System.err.println("Warning: Unsolved or invalid symbol during test method analysis - " + e.getMessage());
            }
            catch (MethodAmbiguityException e) {
                System.err.println("Warning: Ambiguous method call during test method analysis - " + e.getMessage());
            }
        }

        for (FieldAccessExpr fieldAccessExpr : methodDeclaration.findAll(FieldAccessExpr.class)) {
            try {
                if (fieldAccessExpr.resolve().isField()) {
                    ResolvedFieldDeclaration fieldDeclaration = fieldAccessExpr.resolve().asField();
                    testMethodImportance += calculateFieldAccessImportance(graph, fieldDeclaration);
                }
            }
            catch (UnsolvedSymbolException e) {
                System.err.println("Warning: Unsolved or invalid symbol during test method analysis - " + e.getMessage());
            }
            catch (MethodAmbiguityException e) {
                System.err.println("Warning: Ambiguous field access during test method analysis - " + e.getMessage());
            }
        }
        return testMethodImportance;
    }

    private double calculateFieldAccessImportance(RankedGraph<CoverageGraph> graph, ResolvedFieldDeclaration fieldDeclaration) {
        double fieldAccessImportance = 0.0;
        for (Node node : graph.getNodes()) {
            if (coverageManager.isCoveredBy(node, fieldDeclaration)) {
                fieldAccessImportance += node.accept(testCaseVisitor, graph);
            }
        }
        for (Edge edge : graph.getEdges()) {
            if (coverageManager.isCoveredBy(edge, fieldDeclaration)) {
                fieldAccessImportance += edge.accept(testCaseVisitor, graph);
            }
        }
        return fieldAccessImportance;
    }

    private double calculateMethodCallImportance(RankedGraph<CoverageGraph> graph, ResolvedMethodDeclaration calledMethod) {
        double methodCallImportance = 0.0;
        for (Node node : graph.getNodes()) {
            if (coverageManager.isCoveredBy(node, calledMethod)) {
                methodCallImportance += node.accept(testCaseVisitor, graph);
            }
        }
        for (Edge edge : graph.getEdges()) {
            if (coverageManager.isCoveredBy(edge, calledMethod)) {
                methodCallImportance += edge.accept(testCaseVisitor, graph);
            }
        }
        return methodCallImportance;
    }
}