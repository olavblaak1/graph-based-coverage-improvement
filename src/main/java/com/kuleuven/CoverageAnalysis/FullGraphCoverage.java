package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.resolution.MethodAmbiguityException;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.NodeType;
import com.kuleuven.GraphExtraction.ExtractionStrategy.ExtractGraphHelper;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class FullGraphCoverage extends Coverage {

    private Optional<ResolvedType> getImplicitTypeFromFields(NameExpr nameExpr) {
        Optional<ClassOrInterfaceDeclaration> classDeclaration = nameExpr.findAncestor(ClassOrInterfaceDeclaration.class);
        if (classDeclaration.isPresent()) {
            for (FieldDeclaration fieldDeclaration : classDeclaration.get().getFields()) {
                for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariables()) {
                    if (variableDeclarator.getName().equals(nameExpr.getName())) {
                        try {
                            return Optional.of(variableDeclarator.getInitializer().get().calculateResolvedType());
                        } catch (UnsolvedSymbolException e) {
                            System.err.println("Warning: Unsolved symbol during implicit type analysis - " + e.getMessage());
                            return Optional.empty();
                        } catch (NoSuchElementException e) {
                            // This means that the initializer is not present, which is expected for fields
                            // without an initializer.
                            // We can return the type of the field instead.
                            return Optional.of(fieldDeclaration.resolve().getType());
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private Optional<ResolvedType> getImplicitTypeFromMethod(NameExpr nameExpr) {
        Optional<MethodDeclaration> methodDeclaration = nameExpr.findAncestor(MethodDeclaration.class);
        if (methodDeclaration.isPresent()) {
            System.out.println("Has ancestor method");
            System.out.println(methodDeclaration.get().getName());
            for (VariableDeclarationExpr assignExpr : methodDeclaration.get().findAll(VariableDeclarationExpr.class)) {
                for (VariableDeclarator variableDeclarator : assignExpr.getVariables()) {
                    System.out.println("Found variable declaration for: ");
                    System.out.println(variableDeclarator.getName());
                    System.out.println("Comparing to");
                    System.out.println(nameExpr);
                    if (variableDeclarator.getInitializer().isPresent() &&
                            variableDeclarator.getName().equals(nameExpr.getName())) {
                        try {
                            return Optional.of(variableDeclarator.getInitializer().get().calculateResolvedType());
                        } catch (UnsolvedSymbolException e) {
                            System.err.println("Warning: Unsolved symbol during implicit type analysis - " + e.getMessage());
                            return Optional.empty();
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }


    private Optional<ResolvedType> getImplicitTypeOfNameExpr(NameExpr testScope) {
        if (testScope.resolve().isField()) {
            return getImplicitTypeFromFields(testScope);
        }
        if (testScope.resolve().isVariable()) {
            return getImplicitTypeFromMethod(testScope);
        }
        return Optional.empty();
    }


    private Optional<ResolvedType> getImplicitType(MethodCallExpr testCall) {
        return Optional.empty();
    }

    @Override
    protected void analyzeTestMethod(MethodDeclaration testMethod, Collection<MethodDeclaration> testMethods) {
        // Collect all method calls within the test method
        testMethod.findAll(MethodCallExpr.class).forEach(testCall -> {
            try {
                ResolvedMethodDeclaration calledMethod = testCall.resolve();
                getTestMethod(calledMethod, testMethods).ifPresentOrElse(
                        e -> {
                            if (!testMethod.resolve().getQualifiedSignature().equals(calledMethod.getQualifiedSignature())) {
                                analyzeTestMethod(e, testMethods);
                                // If they do equal, we are in a recursive call, so we don't want to analyze it again
                            }
                        },
                        () ->
                                getImplicitType(testCall).ifPresentOrElse(resolvedType -> {
                                            try {
                                                ResolvedReferenceType resolvedClass = resolvedType.asReferenceType();
                                                resolvedClass.getAllMethods().forEach(resolvedMethod -> {
                                                    if (resolvedMethod.getName().equals(calledMethod.getName())) {
                                                        analyzeMethodCall(resolvedMethod);
                                                    }
                                                });
                                            } catch (UnsolvedSymbolException e) {
                                                System.err.println("Warning: Unsolved symbol during implicit type analysis - " + e.getMessage());
                                            }
                                        },
                                        () -> analyzeMethodCall(calledMethod)));
            } catch (UnsolvedSymbolException | IllegalArgumentException | MethodAmbiguityException e) {
                System.err.println("Warning: Unsolved or invalid symbol during test method analysis - " + e.getMessage());
            } catch (UnsupportedOperationException e) {
                // This is expected for method calls that are not resolved, such as static methods
                // or methods from other classes.
                System.err.println("Warning: Unsupported operation during test method analysis - " + e.getMessage());
            } catch (NoSuchElementException e) {
                System.err.println("Warning: No such element during test method analysis - " + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                // I think this is a bug in JavaParser
                System.err.println("Warning: Index out of bounds during test method analysis - " + e.getMessage());
            } catch (IllegalStateException e) {
                System.err.println("Warning: Illegal state during test method analysis - " + e.getMessage());
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

    private void markSubgraph(Graph subgraph) {
        for (Node node : subgraph.getNodes()) {
            markNode(node);
        }
        for (Edge edge : subgraph.getEdges()) {
            markEdge(edge);
        }
    }

    @Override
    protected void analyzeMethodCall(ResolvedMethodDeclaration resolvedTestMethod) {
        if (Graph.getMethodID(resolvedTestMethod).isPresent()) {
            if (!coverageGraph.getNode(Graph.getMethodID(resolvedTestMethod).get()).isPresent()) {
                System.out.println("Warning: Method " + resolvedTestMethod.getQualifiedSignature() + " not found in coverage graph.");
                return;
            }
            Node untestedNode = coverageGraph.getNode(Graph.getMethodID(resolvedTestMethod).get()).get();
            if (isCoveredBy(untestedNode, resolvedTestMethod)) {
                Graph reachableSubgraph = coverageGraph.getReachableSubGraph(untestedNode, List.of(NodeType.METHOD, NodeType.CLASS), List.of(EdgeType.METHOD_CALL, EdgeType.FIELD_ACCESS, EdgeType.OVERRIDES));
                System.out.println(reachableSubgraph.getSize());
                markSubgraph(reachableSubgraph);
            }
        }
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
