package com.kuleuven.GraphExtraction.ExtractionStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodAmbiguityException;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;

import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.GraphExtraction.NodeVisitors.ClassVisitor;
import com.kuleuven.GraphExtraction.NodeVisitors.MethodCallVisitor;
import com.kuleuven.GraphExtraction.NodeVisitors.MethodVisitor;

public class ExtractGraphHelper {

    // Method for extracting class nodes
    public static List<Node> extractClassNodes(List<ClassOrInterfaceDeclaration> nodes) {
        List<Node> graphNodes = new LinkedList<>();
        nodes.forEach(node -> {
            String className = node.getFullyQualifiedName().orElse("Unknown");
            Node graphNode = new ClassNode(className);
            graphNodes.add(graphNode);
        });
        return graphNodes;
    }

    // Method for extracting method nodes
    public static List<Node> extractMethodNodes(List<MethodDeclaration> nodes) {
        List<Node> graphNodes = new LinkedList<>();
        nodes.forEach(node -> {
            MethodNode.OverWrite overwrite = node.getAnnotationByName("Override").isPresent() ? MethodNode.OverWrite.YES : MethodNode.OverWrite.NO;
            String name = node.resolve().getQualifiedName();
            graphNodes.add(new MethodNode(name, overwrite));
        });
        return graphNodes;
    }

    // Method for extracting class definitions from compilation units
    public static List<ClassOrInterfaceDeclaration> getClassesFromCompilationUnits(List<CompilationUnit> compilationUnits) {
        ClassVisitor classVisitor = new ClassVisitor();
        compilationUnits.forEach(cu -> cu.accept(classVisitor, null));
        return new LinkedList<>(classVisitor.getDeclaredClasses());
    }

    // Method for extracting method declarations from compilation units
    public static List<MethodDeclaration> extractMethodsFromCompilationUnits(List<CompilationUnit> compilationUnits) {
        MethodVisitor methodVisitor = new MethodVisitor();
        compilationUnits.forEach(cu -> cu.accept(methodVisitor, null));
        return methodVisitor.getMethodDeclarations();
    }

    // Method for extracting field edges
    public static List<Edge> extractFieldEdges(ClassOrInterfaceDeclaration classDefinition) {
        List<Edge> edges = new LinkedList<>();
        List<ResolvedReferenceType> referencedTypes = new LinkedList<>();

        classDefinition.getFields().forEach(field ->
                field.getVariables().forEach(variableDeclarator -> {
                    ResolvedType type = variableDeclarator.resolve().getType();

                    if (type.isArray()) {
                        type = type.asArrayType().getComponentType();
                    }
                    if (type.isReferenceType()) {
                        referencedTypes.addAll(extractReferencedTypes(type.asReferenceType()));
                    }
                }));

        String className = classDefinition.getFullyQualifiedName().orElse("Unknown");
        referencedTypes.forEach(referencedType -> {
            String referencedClassName = referencedType.describe();
            edges.add(new FieldEdge(className, referencedClassName));
        });

        return edges;
    }

    // Auxiliary method for extracting referenced types
    public static List<ResolvedReferenceType> extractReferencedTypes(ResolvedReferenceType referenceType) {
        List<ResolvedReferenceType> referencedTypes = new LinkedList<>();
        referencedTypes.add(referenceType);

        referenceType.getTypeParametersMap().forEach(pair -> {
            if (pair.b.isReferenceType()) {
                referencedTypes.addAll(extractReferencedTypes(pair.b.asReferenceType()));
            }
        });
        return referencedTypes;
    }

    public static <T extends Resolvable<?>> boolean doesNotResolve(T resolvable) {
        try {
            resolvable.resolve();
            return false;
        } catch (UnsolvedSymbolException e) {
            System.err.println("Could not resolve symbol: " + e.getName() + " in resolvable: " + resolvable);
            return true;
        } catch (MethodAmbiguityException e) {
            System.err.println("Method ambiguity: " + e.getLocalizedMessage() + " in resolvable: " + resolvable);
            return true;
        }
    }



    // Extract method call edges
    public static List<Edge> extractMethodCallEdges(ClassOrInterfaceDeclaration classDefinition) {
        List<Edge> edges = new LinkedList<>();
        String className = classDefinition.getFullyQualifiedName().orElse("Unknown");

        MethodVisitor methodVisitor = new MethodVisitor();
        classDefinition.accept(methodVisitor, null);
        List<MethodDeclaration> methods = methodVisitor.getMethodDeclarations();

        methods.forEach(sourceMethod -> {
            if (doesNotResolve(sourceMethod)) {
                return;
            }
            if (sourceMethod.getNameAsString().contains("java.")) {
                return;
            }
            MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
            sourceMethod.accept(methodCallVisitor, null);
            List<MethodCallExpr> methodCalls = methodCallVisitor.getMethodCalls();

            methodCalls.forEach(methodCall -> {
                if (doesNotResolve(methodCall)) {
                    return;
                }
                String declaringClassName = methodCall.resolve().declaringType().getQualifiedName();
                if (methodCall.resolve().declaringType().getQualifiedName().contains("java.") ||
                        declaringClassName.equals(className)) {
                    return;
                }
                edges.add(new MethodCallEdge(className, declaringClassName));
            });
        });
        return edges;
    }

    // Extract method call edges for MethodDeclaration (used by ExtractMethodGraph)
    public static List<Edge> extractMethodCallEdges(MethodDeclaration methodDeclaration) {
        if (doesNotResolve(methodDeclaration)) {
            return new LinkedList<>();
        }

        MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
        methodDeclaration.accept(methodCallVisitor, null);

        ResolvedMethodDeclaration resolvedNode = methodDeclaration.resolve();

        List<Edge> edges = new LinkedList<>();
        methodCallVisitor.getMethodCalls().forEach(methodCall -> {
            if (doesNotResolve(methodCall)) {
                return;
            }
            ResolvedMethodDeclaration resolvedMethodCall = methodCall.resolve();
            edges.add(new MethodCallEdge(resolvedNode.getQualifiedName(), resolvedMethodCall.getQualifiedName()));
        });

        return edges;
    }


    // Extract inheritance edges
    public static List<Edge> extractInheritanceEdges(ClassOrInterfaceDeclaration classDefinition) {
        List<Edge> edges = new LinkedList<>();
        List<ResolvedReferenceType> inheritedClasses = new LinkedList<>();

        classDefinition.resolve().getAncestors().forEach(ancestor ->
                inheritedClasses.addAll(extractReferencedTypes(ancestor)));

        inheritedClasses.forEach(inheritedClass -> {
            String extendedClassName = inheritedClass.describe();
            String className = classDefinition.getFullyQualifiedName().orElse("Unknown");
            edges.add(new InheritanceEdge(className, extendedClassName));
        });
        return edges;
    }

    public static Optional<Edge> extractOwnsMethodEdge(com.github.javaparser.ast.Node node) {
        if (node instanceof MethodDeclaration) {
            ResolvedMethodDeclaration decl = ((MethodDeclaration) node).resolve();
            return Optional.of(new OwnsMethodEdge(decl.getQualifiedName(), decl.declaringType().getQualifiedName()));
        }
        return Optional.empty();
    }
}