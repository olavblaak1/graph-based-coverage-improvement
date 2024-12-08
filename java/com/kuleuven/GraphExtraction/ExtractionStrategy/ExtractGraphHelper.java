package com.kuleuven.GraphExtraction.ExtractionStrategy;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodAmbiguityException;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.kuleuven.GraphExtraction.ExtractionStrategy.NodeVisitors.MethodCallVisitor;
import com.kuleuven.GraphExtraction.ExtractionStrategy.NodeVisitors.MethodVisitor;
import com.kuleuven.GraphExtraction.Graph.Node;
import com.kuleuven.GraphExtraction.Graph.NodeType;
import com.kuleuven.GraphExtraction.Graph.Edge.Argument;
import com.kuleuven.GraphExtraction.Graph.Edge.Edge;
import com.kuleuven.GraphExtraction.Graph.Edge.FieldEdge;
import com.kuleuven.GraphExtraction.Graph.Edge.InheritanceEdge;
import com.kuleuven.GraphExtraction.Graph.Edge.Method;
import com.kuleuven.GraphExtraction.Graph.Edge.MethodCallEdge;

public class ExtractGraphHelper {

    public static Set<Edge> extractUniqueMethodCallEdges(ClassOrInterfaceDeclaration classDefinition) throws UnsolvedSymbolException {
        Set<Edge> edges = new HashSet<>();
        String className = classDefinition.getFullyQualifiedName().orElse("Unknown");

        MethodVisitor methodVisitor = new MethodVisitor();
        classDefinition.accept(methodVisitor, null);
        List<MethodDeclaration> methods = methodVisitor.getMethodDeclarations();
        
        methods.forEach(sourceMethod -> {
            if (sourceMethod.getNameAsString().contains("java.")) {
                return;
            }
            MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
            sourceMethod.accept(methodCallVisitor, null);
            List<MethodCallExpr> methodCalls = methodCallVisitor.getMethodCalls();
            methodCalls.forEach(methodCall -> {

                try {
                    methodCall.resolve();
                } catch (UnsolvedSymbolException e) {
                    System.err.println("Could not resolve symbol: " + e.getName() + " in method call: " + methodCall);
                    return;
                } catch (MethodAmbiguityException e) {
                    System.err.println("Method ambiguity: " + e.getLocalizedMessage() + " in method call: " + methodCall);
                    return;
                }


                String declaringClassName = methodCall.resolve().declaringType().getQualifiedName();
                if (methodCall.resolve().declaringType().getQualifiedName().contains("java.") || 
                    declaringClassName.equals(className)) {
                    return;
                }

                Node sourceNode = new Node(className, NodeType.CLASS);
                Node destinationNode = new Node(declaringClassName, NodeType.CLASS);

                String linkMethodSignature      = methodCall.resolve().getSignature();
                String linkMethodName           = methodCall.getNameAsString();
                String linkMethodReturnType     = methodCall.resolve().getReturnType().describe();
                String linkMethodDeclaringClass = methodCall.resolve().declaringType().getQualifiedName();
                List<Argument> linkArguments = getArguments(methodCall);
                Method linkMethod = new Method(linkMethodSignature, linkMethodName, linkMethodReturnType, linkMethodDeclaringClass, linkArguments);


                String sourceMethodSignature      = sourceMethod.getSignature().asString();
                String sourceMethodName           = sourceMethod.getNameAsString();
                String sourceMethodReturnType     = sourceMethod.getType().resolve().describe();
                String sourceMethodDeclaringClass = className;
                List<Argument> sourceMethodArguments = getArguments(sourceMethod);
                Method sMethod = new Method(sourceMethodSignature, sourceMethodName, sourceMethodReturnType, sourceMethodDeclaringClass, sourceMethodArguments);
                

                MethodCallEdge edge = new MethodCallEdge(
                    sourceNode,
                    destinationNode,
                    linkMethod,
                    sMethod
                );
                edges.add(edge);
            });
        });
        return edges;
    }

    
    private static List<Argument> getArguments(MethodCallExpr methodCallExpr) {
        List<Argument> arguments = new LinkedList<>();
        methodCallExpr.getArguments().forEach(arg -> {
            arguments.add(new Argument(arg.toString(), arg.calculateResolvedType().describe()));
        });
        return arguments;
    }

    // These are technically the parameters of the method, not the arguments..
    private static List<Argument> getArguments(MethodDeclaration methodDeclaration) {
        List<Argument> parameters = new LinkedList<>();
        methodDeclaration.getParameters().forEach(param -> {
            parameters.add(new Argument(param.getNameAsString(), param.getType().resolve().describe()));
        });
        return parameters;
    }


    public static List<Edge> extractInheritanceEdges(ClassOrInterfaceDeclaration classDefinition) {
        List<Edge> edges = new LinkedList<>();
        List<ResolvedReferenceType> inheritedClasses = new LinkedList<>();

        classDefinition.resolve().getAncestors().forEach(ancestor -> {
            inheritedClasses.addAll(extractReferencedTypes(ancestor));
        });

        inheritedClasses.forEach(inheritedClass -> {
            String extendedClassName = inheritedClass.describe();

            String className = classDefinition.getFullyQualifiedName().orElse("Unknown");
            Node subclass = new Node(className, NodeType.CLASS);
            Node superclass = new Node(extendedClassName, NodeType.CLASS);

            Edge edge = new InheritanceEdge(subclass, superclass);
            edges.add(edge);
        });
        return edges;
    }


    /**
     * Extracts the edges from one class to another if the class holds this class as a type in a field
     * @param classDefinition
     * @return 
     */
    public static List<Edge> extractFieldEdges(ClassOrInterfaceDeclaration classDefinition) {
        List<Edge> edges = new LinkedList<>();
        
        List<ResolvedReferenceType> referencedTypes = new LinkedList<>();
        classDefinition.getFields().forEach(field -> {
            field.getVariables().forEach(variableDeclarator -> {
                ResolvedType type = variableDeclarator.resolve().getType();

                if (type.isArray()) {
                    type = type.asArrayType().getComponentType();
                }
                if (type.isReferenceType()) {
                    referencedTypes.addAll(extractReferencedTypes(type.asReferenceType()));
                }
            });
        });


        String className = classDefinition.getFullyQualifiedName().orElse("Unknown");
        referencedTypes.forEach(referencedType -> {
            String referencedClassName = referencedType.describe();
            Node sourceNode = new Node(className, NodeType.CLASS);
            Node destinationNode = new Node(referencedClassName, NodeType.CLASS);
            Edge edge = new FieldEdge(sourceNode, destinationNode);
            edges.add(edge);
        });

        return edges;
    }


    /**
     * Extracts all types referenced by a reference type, for example, List<String> would return List and String as ResolvedReferenceTypes
     * @param referenceType
     * @return  a list of ResolvedReferenceTypes that are referenced by the input reference type
     */
    private static List<ResolvedReferenceType> extractReferencedTypes(ResolvedReferenceType referenceType) {
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


}
