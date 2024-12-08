package com.kuleuven.GraphExtraction.Extraction.ExtractionStrategy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.kuleuven.GraphExtraction.Extraction.NodeVisitors.MethodCallVisitor;
import com.kuleuven.GraphExtraction.Extraction.NodeVisitors.MethodVisitor;
import com.kuleuven.GraphExtraction.Graph.Edge.Edge;
import com.kuleuven.GraphExtraction.Graph.Edge.Method;
import com.kuleuven.GraphExtraction.Graph.Edge.MethodCallEdge;
import com.kuleuven.GraphExtraction.Graph.MethodNode;
import com.kuleuven.GraphExtraction.Graph.Node;

public class ExtractMethodGraph extends ExtractionTemplate<MethodDeclaration> {

    @Override
    public Collection<Edge> extractEdges(List<MethodDeclaration> nodes) {
        List<Edge> edges = new java.util.LinkedList<>();
        nodes.forEach(node -> {
            edges.addAll(extractMethodCallEdges(node));
        });
        return edges;
    }

    @Override
    public List<MethodDeclaration> extractASTNodes(List<CompilationUnit> compilationUnits) {
        MethodVisitor methodVisitor = new MethodVisitor();
        compilationUnits.forEach(cu -> {
            cu.accept(methodVisitor, null);
        });
        return methodVisitor.getMethodDeclarations();
    }

    @Override
    protected List<com.kuleuven.GraphExtraction.Graph.Node> convertNodes(List<MethodDeclaration> nodes) {
        List<Node> graphNodes = new LinkedList<>();
        nodes.forEach(node -> {
            graphNodes.add(new MethodNode(node.resolve().getQualifiedName()));
        });
        return graphNodes;
    }

    
    private List<Edge> extractMethodCallEdges(MethodDeclaration node) {
        if(!ExtractGraphHelper.resolves(node)) {
            return new LinkedList<>();
        }

        MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
        node.accept(methodCallVisitor, null);

        Node sourceNode = new MethodNode(node.resolve().getQualifiedName());
        Method destinationMethod = new Method(node.resolve());

        List<Edge> edges = new LinkedList<>();
        methodCallVisitor.getMethodCalls().forEach(methodCall -> {
            if (!ExtractGraphHelper.resolves(methodCall)) {
                return;
            }

            Node destinationNode = new MethodNode(methodCall.resolve().getQualifiedName());
            Method sourceMethod = new Method(methodCall.resolve());

            edges.add(new MethodCallEdge(sourceNode, destinationNode, sourceMethod, destinationMethod));
        });

        return edges;
    }

}
