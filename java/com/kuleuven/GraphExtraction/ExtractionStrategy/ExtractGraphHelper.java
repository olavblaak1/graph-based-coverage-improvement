package com.kuleuven.GraphExtraction.ExtractionStrategy;

import java.util.LinkedList;
import java.util.List;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodAmbiguityException;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.kuleuven.GraphExtraction.NodeVisitors.MethodCallVisitor;
import com.kuleuven.GraphExtraction.NodeVisitors.MethodVisitor;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.MethodCallEdge;
import com.kuleuven.Graph.ClassNode;
import com.kuleuven.Graph.Node;

class ExtractGraphHelper {


    // This method is only here because Original and InheritanceFields both need it
    static List<Edge> extractMethodCallEdges(ClassOrInterfaceDeclaration classDefinition)
            throws UnsolvedSymbolException {
        List<Edge> edges = new LinkedList<>();
        String className = classDefinition.getFullyQualifiedName().orElse("Unknown");

        MethodVisitor methodVisitor = new MethodVisitor();
        classDefinition.accept(methodVisitor, null);
        List<MethodDeclaration> methods = methodVisitor.getMethodDeclarations();

        methods.forEach(sourceMethod -> {
            if (!resolves(sourceMethod)) {
                return;
            }

            if (sourceMethod.getNameAsString().contains("java.")) {
                return;
            }


            MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
            sourceMethod.accept(methodCallVisitor, null);
            List<MethodCallExpr> methodCalls = methodCallVisitor.getMethodCalls();


            methodCalls.forEach(methodCall -> {
                if(!resolves(methodCall)) {
                    return;
                }

                String declaringClassName = methodCall.resolve().declaringType().getQualifiedName();
                if (methodCall.resolve().declaringType().getQualifiedName().contains("java.") ||
                        declaringClassName.equals(className)) {
                    return;
                }

                Node sourceNode = new ClassNode(className);
                Node destinationNode = new ClassNode(declaringClassName);

                MethodCallEdge edge = new MethodCallEdge(
                        sourceNode,
                        destinationNode); 
                edges.add(edge);
            });
        });
        return edges;
    }

    /**
     * Extracts all types referenced by a reference type, for example, List<String>
     * would return List and String as ResolvedReferenceTypes
     * 
     * @param referenceType
     * @return a list of ResolvedReferenceTypes that are referenced by the input
     *         reference type
     */
    static List<ResolvedReferenceType> extractReferencedTypes(ResolvedReferenceType referenceType) {
        List<ResolvedReferenceType> referencedTypes = new LinkedList<>();
        referencedTypes.add(referenceType);

        // Recursively handle type parameters (generics)
        referenceType.getTypeParametersMap().forEach(pair -> { // pair.a = type parameter, pair.b = type
            if (pair.b.isReferenceType()) {
                referencedTypes.addAll(extractReferencedTypes(pair.b.asReferenceType()));
            }
        });
        return referencedTypes;
    }


    /**
     * Attempts to resolve a resolvable object and returns whether the resolution was successful.
     * 
     * @param <T> the type of the resolvable object
     * @param resolvable the resolvable object to resolve
     * @return true if the resolution was successful, false otherwise
     */
    static <T extends Resolvable<?>> boolean resolves(T resolvable) {
        try {
            resolvable.resolve();
            return true;
        } catch (UnsolvedSymbolException e) {
            System.err.println("Could not resolve symbol: " + e.getName() + " in resolvable: " + resolvable);
            return false;
        } catch (MethodAmbiguityException e) {
            System.err.println("Method ambiguity: " + e.getLocalizedMessage() + " in resolvable: " + resolvable);
            return false;
        }
    }

}
