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


    public Map<TestCase, Double> minimizeTests(RankedGraph<CoverageGraph> graph, Collection<MethodDeclaration> testMethods, Collection<MethodDeclaration> privateMethods) {
        Map<TestCase, Double> minimizedTests = new HashMap<>();
        testMethods.forEach(methodDeclaration ->
                minimizedTests.put(new TestCase(methodDeclaration), analyzeTestMethod(methodDeclaration, graph, testMethods, privateMethods)));
        return minimizedTests;
    }

    private Optional<MethodDeclaration> getTestMethod(ResolvedMethodDeclaration methodDeclaration, Collection<MethodDeclaration> testMethods, Collection<MethodDeclaration> privateMethods) {
        for (MethodDeclaration testMethod : testMethods) {
            if (testMethod.resolve().getQualifiedSignature().equals(methodDeclaration.getQualifiedSignature())) {
                return Optional.of(testMethod);
            }
        }
        for (MethodDeclaration privateMethod : privateMethods) {
            if (privateMethod.resolve().getQualifiedSignature().equals(methodDeclaration.getQualifiedSignature())) {
                return Optional.of(privateMethod);
            }
        }
        return Optional.empty();
    }

    private double analyzeTestMethod(MethodDeclaration methodDeclaration, RankedGraph<CoverageGraph> graph, Collection<MethodDeclaration> testMethods, Collection<MethodDeclaration> privateMethods) {
        double testMethodImportance = 0.0;
        for (MethodCallExpr methodCallExpr : methodDeclaration.findAll(MethodCallExpr.class)) {
            try {
                ResolvedMethodDeclaration calledMethod = methodCallExpr.resolve();
                // We need to check if the called method is a test method, because if so,
                // we need to also analyze the effects of that test method that is called,
                // otherwise structural methods (methods in a test suite that only call other test methods) have an importance of 0.
                Optional<MethodDeclaration> correspondingTestMethod = getTestMethod(calledMethod, testMethods, privateMethods);
                if (correspondingTestMethod.isPresent()) {
                    testMethodImportance += analyzeTestMethod(correspondingTestMethod.get(), graph, testMethods, privateMethods);
                } else {
                    testMethodImportance += calculateMethodCallImportance(graph, calledMethod);
                }
            } catch (UnsolvedSymbolException e) {
                System.err.println("Warning: Unsolved or invalid symbol during test method analysis - " + e.getMessage());
            } catch (MethodAmbiguityException e) {
                System.err.println("Warning: Ambiguous method call during test method analysis - " + e.getMessage());
            } catch (UnsupportedOperationException e) {
                // This is expected for method calls that are not resolved, such as static methods
                // or methods from other classes.
                System.err.println("Warning: Unsupported operation during test method analysis - " + e.getMessage());
            } catch (NoSuchElementException e) {
                // This is expected for method calls that are not resolved, such as static methods
                // or methods from other classes.
                System.err.println("Warning: No such element during test method analysis - " + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                // I think this is a bug in JavaParser
                System.err.println("Warning: Index out of bounds during test method analysis - " + e.getMessage());
            } catch (Exception e ) {
                System.err.println("Warning: Unexpected exception during test method analysis - " + e.getMessage());
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
        if (methodDeclaration.resolve().getQualifiedName().equals("org.jfree.chart.XYStepChartTest.testSetSeriesToolTipGenerator")) {
            System.out.println("Importance of THE method: " + testMethodImportance);
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

        /* subgraph method
        Optional<String> methodID = CoverageGraph.getMethodID(calledMethod);

        if (methodID.isEmpty()) {
            return 0.0;
        }

        Optional<Node> testNode = graph.getGraph().getNode(methodID.get());
        if (testNode.isEmpty()) {
            return 0.0;
        }

        Graph reachableSubgraph = graph.getGraph().getReachableSubGraph(testNode.get(), List.of(NodeType.METHOD, NodeType.CLASS), List.of(EdgeType.METHOD_CALL, EdgeType.FIELD_ACCESS, EdgeType.OVERRIDES, EdgeType.OWNED_BY));
        for (Node node : reachableSubgraph.getNodes()) {
            methodCallImportance += node.accept(graphImportanceVisitor, graph);
        }
        for (Edge edge : reachableSubgraph.getEdges()) {
            methodCallImportance += edge.accept(graphImportanceVisitor, graph);
        }
        */
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