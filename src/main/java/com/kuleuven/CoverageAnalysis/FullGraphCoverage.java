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
import com.kuleuven.Graph.Graph.Graph;

import java.util.List;
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


    /*
    This method is a band-aid solution for a limitation of JavaParser being a static analysis tool:
    It will always resolve to the DECLARED type of an object, which may not be the concrete type
    during execution. Dynamic analysis would complement this approach very well,
    but it is also possible to cover some simple cases using static analysis like done here
     */
    private Optional<ResolvedType> getImplicitType(MethodCallExpr testCall) {
        if (testCall.resolve().getName().equals("getPosition")) {
            System.out.println("Checking implicit type of getPositionmethodCallExpr");
            if (testCall.getScope().isPresent() && testCall.getScope().get().isNameExpr()) {
                return getImplicitTypeOfNameExpr(testCall.getScope().get().asNameExpr());
            }
        }
        return Optional.empty();
    }


    @Override
    protected void analyzeTestMethod(MethodDeclaration testMethod, List<MethodDeclaration> testMethods) {
        // Collect all method calls within the test method
        testMethod.findAll(MethodCallExpr.class).forEach(testCall -> {
            try {
                ResolvedMethodDeclaration calledMethod = testCall.resolve();
                getTestMethod(calledMethod, testMethods).ifPresentOrElse(
                        e -> analyzeTestMethod(e, testMethods),
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
