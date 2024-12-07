package com.kuleuven.GraphExtraction.ExtractionStrategy;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.kuleuven.GraphExtraction.GraphUtils;
import com.kuleuven.GraphExtraction.ExtractionStrategy.NodeVisitors.FieldTypesVisitor;
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

    public static List<Edge> extractUniqueMethodCallEdges(ClassOrInterfaceDeclaration classDefinition) {
        List<Edge> edges = new LinkedList<>();
        Set<String> uniqueEdges = new HashSet<>();
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
                if (methodCall.resolve().declaringType().getQualifiedName().contains("java.")) {
                    return;
                }
                String declaringClassName = methodCall.resolve().declaringType().getQualifiedName();
                String uniqueId = GraphUtils.getUniqueId(methodCall, sourceMethod, className);

                if (!uniqueEdges.contains(uniqueId) && !declaringClassName.equals(className)) {
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
                    uniqueEdges.add(uniqueId);
                }
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
        Set<Edge> uniqueEdges = new HashSet<>();

        List<ResolvedReferenceType> inheritedClasses = new LinkedList<>();
        classDefinition.resolve().getAncestors().forEach(ancestor -> {
            inheritedClasses.add(ancestor);
        });

        inheritedClasses.forEach(inheritedClass -> {
            String extendedClassName = inheritedClass.describe();

            String className = classDefinition.getFullyQualifiedName().orElse("Unknown");
            Node subclass = new Node(className, NodeType.CLASS);
            Node superclass = new Node(extendedClassName, NodeType.CLASS);

            Edge edge = new InheritanceEdge(subclass, superclass);
            if (!uniqueEdges.contains(edge)) {
                edges.add(edge);
                uniqueEdges.add(edge);
            }
        });
        return edges;
    }


    /**
     * Extracts the edges from one class to another if the class holds this class as a type in a field
     * TODO: ADD SUPPORT FOR FIELDS THAT ARE GENERIC REFERENCES TO OTHER CLASSES (ex. List<Employee>)
     * @param classDefinition
     * @return 
     */
    public static List<Edge> extractFieldEdges(ClassOrInterfaceDeclaration classDefinition) {
        List<Edge> edges = new LinkedList<>();
        Set<Edge> uniqueEdges = new HashSet<>();

        String className = classDefinition.getFullyQualifiedName().orElse("Unknown");
        
        List<String> refersTo = new LinkedList<>();
        classDefinition.getFields().forEach(field -> {

            // In case the field refers to a generic type, we take the type inside by recursively
            // tracing the type 
            
            FieldTypesVisitor typesVisitor = new FieldTypesVisitor();
            typesVisitor.visit(field, null);
            
            typesVisitor.getReferredTypes().forEach(type -> {
                refersTo.add(type.describe());
            });

            refersTo.forEach(type -> {
                Node sourceNode = new Node(className, NodeType.CLASS);
                Node destinationNode = new Node(type, NodeType.CLASS);
    
                Edge edge = new FieldEdge(sourceNode, destinationNode);
                if (!uniqueEdges.contains(edge)) {
                    edges.add(edge);
                    uniqueEdges.add(edge);
                }
            });
        });

        return edges;
    }
}
