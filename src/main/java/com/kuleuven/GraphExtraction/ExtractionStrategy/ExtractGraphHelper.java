package com.kuleuven.GraphExtraction.ExtractionStrategy;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodAmbiguityException;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.isOverride;
import com.kuleuven.GraphExtraction.NodeVisitors.ClassVisitor;
import com.kuleuven.GraphExtraction.NodeVisitors.FieldAccessVisitor;
import com.kuleuven.GraphExtraction.NodeVisitors.MethodCallVisitor;
import com.kuleuven.GraphExtraction.NodeVisitors.MethodVisitor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
        nodes.forEach(node -> createMethodNode(node).ifPresent(graphNodes::add));
        return graphNodes;
    }

    public static Optional<MethodNode> createMethodNode(MethodDeclaration node) {
        try {
            ResolvedMethodDeclaration resolvedMethod = node.resolve();
            String name = node.resolve().getQualifiedName();
            String signature = node.resolve().getSignature();

            isOverride overwrite = node.getAnnotationByName("Override").isPresent() ? isOverride.YES : isOverride.NO;
            if (overwrite == isOverride.YES) {
                Optional<ResolvedMethodDeclaration> overriddenMethod = Optional.empty();
                if (resolvedMethod.declaringType().isEnum()) {
                    // Because enum constants can actually override methods inside the same enum (thus not in its parents)
                    // we need to handle this case separately
                    overriddenMethod = getEnumOverriddenMethod(resolvedMethod);
                }
                if (!overriddenMethod.isPresent()) {
                    // If the method is not an enum or is not overriding a method in the same enum, we can look for
                    // overridden methods in the ancestor(s) (enums can also implement Interfaces)
                    overriddenMethod = getOverriddenMethod(resolvedMethod);
                }

                if (!overriddenMethod.isPresent()) {
                    // If it is finally not found, there must be an edge-case that we did not consider
                    System.out.println("Could not find overridden method for: " + name);
                    return Optional.empty();
                }
                MethodNode overriddenMethodNode = new MethodNode(overriddenMethod.get().getQualifiedName(),
                        overriddenMethod.get().getSignature());
                return Optional.of(new MethodNode(name, overwrite, signature, overriddenMethodNode.getId()));
            }

            return Optional.of(new MethodNode(name, signature, overwrite));
        } catch (UnsolvedSymbolException e) {
            System.err.println("Could not resolve symbol: " + e.getName() + " in method: " + node);
            return Optional.empty();
        } catch (MethodAmbiguityException e) {
            System.err.println("Method ambiguity: " + e.getLocalizedMessage() + " in method: " + node);
            return Optional.empty();
        } catch (UnsupportedOperationException e) {
            System.err.println("Cannot resolve: " + e.getLocalizedMessage() + " in method: " + node);
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            System.err.println("This is likely a bug in JavaParser: " + e.getMessage());
            return Optional.empty();
        }

    }

    private static Optional<ResolvedMethodDeclaration> getEnumOverriddenMethod(ResolvedMethodDeclaration resolvedMethod) {
        for (ResolvedMethodDeclaration method : resolvedMethod.declaringType().getDeclaredMethods()) {
            if (isMethodOverride(method, resolvedMethod)) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
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
        referencedTypes.forEach(referencedType ->
                edges.add(new FieldEdge(new ClassNode(className), new ClassNode(referencedType.getQualifiedName()))));

        return edges;
    }


    private static Optional<ResolvedMethodDeclaration> getOverriddenMethod(ResolvedMethodDeclaration methodDeclaration) {
        for (ResolvedReferenceType ancestor : methodDeclaration.declaringType().getAncestors()) {
            for (ResolvedMethodDeclaration method : ancestor.getAllMethodsVisibleToInheritors()) {
                if (isMethodOverride(method, methodDeclaration)) {
                    return Optional.of(method); // Found a match, return immediately
                }
            }
            Optional<ResolvedMethodDeclaration> overridden = getOverriddenMethodFromAncestor(ancestor, methodDeclaration);
            if (overridden.isPresent()) {
                return overridden;
            }
        }
        return Optional.empty();
    }

    private static Optional<ResolvedMethodDeclaration> getOverriddenMethodFromAncestor(
            ResolvedReferenceType ancestor, ResolvedMethodDeclaration methodDeclaration) {
        for (ResolvedMethodDeclaration method : ancestor.getAllMethodsVisibleToInheritors()) {
            if (isMethodOverride(method, methodDeclaration)) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    private static boolean isMethodOverride(ResolvedMethodDeclaration method, ResolvedMethodDeclaration methodDeclaration) {
        if (method.getSignature().equals(methodDeclaration.getSignature())) {
            return true;
        }

        if (methodDeclaration.getNumberOfParams() == method.getNumberOfParams() &&
                methodDeclaration.getName().equals(method.getName())) {
            for (int i = 0; i < methodDeclaration.getNumberOfParams(); i++) {
                ResolvedParameterDeclaration methodParam = methodDeclaration.getParam(i);
                ResolvedParameterDeclaration ancestorParam = method.getParam(i);
                try {
                    if (!ancestorParam.getType().erasure().isAssignableBy(methodParam.getType().erasure())) {
                        return false;
                    }
                }
                catch (IllegalArgumentException e) {
                    // This is likely a bug in JavaParser, we can ignore it for now
                    return false;
                }

            }
            return true;
        }
        return false;
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
        } catch (UnsupportedOperationException e) {
            System.err.println("Cannot resolve: " + e.getLocalizedMessage() + " in resolvable: " + resolvable);
            return true;
        } catch (IllegalStateException e) {
            System.err.println("Illegal state: " + e.getLocalizedMessage() + " in resolvable: " + resolvable);
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument: " + e.getLocalizedMessage() + " in resolvable: " + resolvable);
            return true;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getLocalizedMessage() + " in resolvable: " + resolvable);
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
                ResolvedMethodDeclaration resolvedCall = methodCall.resolve();
                String declaringClassName = resolvedCall.declaringType().getQualifiedName();
                if (declaringClassName.contains("java.") ||
                        declaringClassName.equals(className)) {
                    return;
                }

                MethodNode destinationMethodNode = new MethodNode(resolvedCall.getQualifiedName(),
                        resolvedCall.getSignature());

                ResolvedMethodDeclaration resolvedSourceMethod = sourceMethod.resolve();
                MethodNode sourceMethodNode = new MethodNode(resolvedSourceMethod.getQualifiedName(),
                        resolvedSourceMethod.getSignature());


                edges.add(new MethodCallEdge(sourceMethodNode, destinationMethodNode));
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

        List<Edge> edges = new LinkedList<>();
        methodCallVisitor.getMethodCalls().forEach(methodCall -> {
            if (doesNotResolve(methodCall)) {
                return;
            }

            try {

                ResolvedMethodDeclaration resolvedMethodCall = methodCall.resolve();

                MethodNode destinationMethodNode = new MethodNode(resolvedMethodCall.getQualifiedName(),
                        resolvedMethodCall.getSignature());

                ResolvedMethodDeclaration resolvedSourceMethod = methodDeclaration.resolve();

                MethodNode sourceMethodNode = new MethodNode(resolvedSourceMethod.getQualifiedName(),
                        resolvedSourceMethod.getSignature());

                edges.add(new MethodCallEdge(sourceMethodNode, destinationMethodNode));
            } catch (UnsolvedSymbolException e) {
                System.err.println("Could not resolve symbol: " + e.getName() + " in method: " + methodDeclaration);
            } catch (MethodAmbiguityException e) {
                System.err.println("Method ambiguity: " + e.getLocalizedMessage() + " in method: " + methodDeclaration);
            } catch (UnsupportedOperationException e) {
                System.err.println("Cannot resolve: " + e.getLocalizedMessage() + " in method: " + methodDeclaration);
            }
        });

        return edges;
    }

    public static List<Edge> extractOverridesEdges(MethodDeclaration methodDeclaration) {
        List<Edge> edges = new LinkedList<>();
        try {
            Optional<ResolvedMethodDeclaration> overridden = getOverriddenMethod(methodDeclaration.resolve());
            if (overridden.isPresent()) {
                MethodNode overriddenNode = new MethodNode(overridden.get().getQualifiedName(), overridden.get().getSignature());
                MethodNode overridingNode = new MethodNode(methodDeclaration.resolve().getQualifiedName(), methodDeclaration.resolve().getSignature());
                edges.add(new OverridesEdge(overridingNode, overriddenNode));
            }
        } catch (UnsolvedSymbolException e) {
            System.err.println("Could not resolve symbol: " + e.getName() + " in method: " + methodDeclaration);
        } catch (MethodAmbiguityException e) {
            System.err.println("Method ambiguity: " + e.getLocalizedMessage() + " in method: " + methodDeclaration);
        } catch (UnsupportedOperationException e) {
            System.err.println("Cannot resolve: " + e.getLocalizedMessage() + " in method: " + methodDeclaration);
        }
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
            if (!className.equals(extendedClassName)) {
                edges.add(new InheritanceEdge(className, extendedClassName));
            }
        });
        return edges;
    }

    public static Optional<Edge> extractOwnsMethodEdge(com.github.javaparser.ast.Node node) {
        if (node instanceof MethodDeclaration) {
            ResolvedMethodDeclaration decl = ((MethodDeclaration) node).resolve();

            try {
                return Optional.of(new OwnedByEdge(new MethodNode(decl.getQualifiedName(), decl.getSignature()),
                        new ClassNode(decl.declaringType().getQualifiedName())));
            } catch (UnsolvedSymbolException e) {
                System.err.println("Could not resolve symbol: " + e.getName() + " in method: " + node);
            } catch (MethodAmbiguityException e) {
                System.err.println("Method ambiguity: " + e.getLocalizedMessage() + " in method: " + node);
            } catch (UnsupportedOperationException e) {
                System.err.println("Cannot resolve: " + e.getLocalizedMessage() + " in method: " + node);
            }
        }
        return Optional.empty();
    }

    public static List<Edge> extractFieldAccessEdge(com.github.javaparser.ast.Node node) {
        List<Edge> edges = new LinkedList<>();
        if (node instanceof MethodDeclaration) {
            MethodDeclaration decl = ((MethodDeclaration) node);
            FieldAccessVisitor fieldAccessVisitor = new FieldAccessVisitor();
            decl.accept(fieldAccessVisitor, null);
            fieldAccessVisitor.getAccessExprs().forEach(accessExpr -> {
                if (doesNotResolve(accessExpr)) {
                    return;
                }

                if (accessExpr.resolve().isField()) {
                    ClassNode accessedClass = new ClassNode(accessExpr.resolve().asField().declaringType().getQualifiedName());
                    createMethodNode(decl).ifPresent(method -> edges.add(new FieldAccessEdge(method, accessedClass)));
                }
            });
        }
        return edges;
    }
}