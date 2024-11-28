package com.kuleuven.GraphExtraction;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import com.kuleuven.GraphExtraction.Graph.Node;
import com.kuleuven.GraphExtraction.Graph.Edge.Argument;
import com.kuleuven.GraphExtraction.Graph.Edge.Edge;
import com.kuleuven.GraphExtraction.Graph.Edge.MethodCallEdge;
import com.kuleuven.GraphExtraction.Graph.Edge.Method;
import com.kuleuven.GraphExtraction.Graph.NodeType;


/**
 * Original Graph Extraction method from Charles Sys' Thesis
 * 
 * With this strategy, the nodes of the resulting graph are CLASS DEFINITIONS
 * and the edges are METHOD CALLS between these classes, ignoring method calls within a class.
 * This one includes edges with imported classes, but not as nodes.
 */
public class ExtractGraphOriginal extends ExtractionTemplate {

    /**
     * Extracts the edges of the graph from the list of Nodes
     * 
     * @param nodes: the list of Nodes to extract the edges from
     * @return the edges of the graph, which are the method calls between classes, ignoring method calls within a class
     */
    @Override
    public List<Edge> extractEdges(List<com.github.javaparser.ast.Node> nodes) {
        Set<String> uniqueEdges = new HashSet<>();
        List<Edge> edges = new LinkedList<>();
        nodes.forEach(node -> {
        if (node instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration classDefinition = (ClassOrInterfaceDeclaration) node;
            String className = classDefinition.getFullyQualifiedName().orElse("Unknown");

            MethodVisitor methodVisitor = new MethodVisitor();
            classDefinition.accept(methodVisitor, null);
            List<MethodDeclaration> methods = methodVisitor.getMethodDeclarations();
            
            methods.forEach(sourceMethod -> {;
                MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
                sourceMethod.accept(methodCallVisitor, null);
                List<MethodCallExpr> methodCalls = methodCallVisitor.getMethodCalls();
                methodCalls.forEach(methodCall -> {
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
        }
        });
        return edges;
    }

    private List<Argument> getArguments(MethodCallExpr methodCallExpr) {
        List<Argument> arguments = new LinkedList<>();
        methodCallExpr.getArguments().forEach(arg -> {
            arguments.add(new Argument(arg.toString(), arg.calculateResolvedType().describe()));
        });
        return arguments;
    }

    // These are technically the parameters of the method, not the arguments..
    private List<Argument> getArguments(MethodDeclaration methodDeclaration) {
        List<Argument> parameters = new LinkedList<>();
        methodDeclaration.getParameters().forEach(param -> {
            parameters.add(new Argument(param.getNameAsString(), param.getType().resolve().describe()));
        });
        return parameters;
    }

    /**
     * Extracts the AST's nodes, which in this case are the CLASS DEFINITIONS
     * 
     * @param cu: the CompilationUnit of the Java source file
     * @return the list of AST nodes, which are the class definitions
     * 
     */
    @Override
    public List<com.github.javaparser.ast.Node> extractASTNodes(CompilationUnit cu) {
        List<com.github.javaparser.ast.Node> nodes = new LinkedList<>();
        ClassVisitor classVisitor = new ClassVisitor();
        cu.accept(classVisitor, null);
        List<ClassOrInterfaceDeclaration> classDefinitions = classVisitor.getDeclaredClasses();
        nodes.addAll(classDefinitions);
        return nodes;
    }

    @Override
    public List<Node> convertNodes(List<com.github.javaparser.ast.Node> nodes) {
        List<Node> graphNodes = new LinkedList<>();
        nodes.forEach(node -> {
            if (node instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classDefinition = (ClassOrInterfaceDeclaration) node;
                String className = classDefinition.getFullyQualifiedName().orElse("Unknown");
                Node graphNode = new Node(className, NodeType.CLASS);
                graphNodes.add(graphNode);
            }
        });
        return graphNodes;
    }
}
