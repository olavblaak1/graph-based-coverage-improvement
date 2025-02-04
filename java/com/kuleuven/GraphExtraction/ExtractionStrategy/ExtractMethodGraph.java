package com.kuleuven.GraphExtraction.ExtractionStrategy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.GraphExtraction.NodeVisitors.MethodCallVisitor;
import com.kuleuven.GraphExtraction.NodeVisitors.MethodVisitor;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.MethodNode;
import com.kuleuven.Graph.MethodNode.OverWrite;
import com.kuleuven.Graph.Node;
import com.kuleuven.Graph.Edge.MethodCallEdge;

public class ExtractMethodGraph extends ExtractionTemplate<MethodDeclaration> {

    @Override
    public Collection<Edge> extractEdges(List<MethodDeclaration> nodes) {
        List<Edge> edges = new java.util.LinkedList<>();
        nodes.forEach(node -> edges.addAll(extractMethodCallEdges(node)));
        return edges;
    }

    @Override
    public List<MethodDeclaration> extractASTNodes(List<CompilationUnit> compilationUnits) {
        MethodVisitor methodVisitor = new MethodVisitor();
        compilationUnits.forEach(cu -> cu.accept(methodVisitor, null));
        return methodVisitor.getMethodDeclarations();
    }

    @Override
    protected List<com.kuleuven.Graph.Node> convertNodes(List<MethodDeclaration> nodes) {
        List<Node> graphNodes = new LinkedList<>();
        nodes.forEach(node -> {
            OverWrite overwrite = node.getAnnotationByName("Override").isPresent() ? OverWrite.YES : OverWrite.NO;
            ResolvedMethodDeclaration resolvedNode = node.resolve();
            String name = resolvedNode.getQualifiedName();
            graphNodes.add(new MethodNode(name, overwrite));
        });
        return graphNodes;
    }

    
    private List<Edge> extractMethodCallEdges(MethodDeclaration node) {
        if(ExtractGraphHelper.doesNotResolve(node)) {
            return new LinkedList<>();
        }

        MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
        node.accept(methodCallVisitor, null);

        ResolvedMethodDeclaration resolvedNode = node.resolve();

        List<Edge> edges = new LinkedList<>();
        methodCallVisitor.getMethodCalls().forEach(methodCall -> {
            if (ExtractGraphHelper.doesNotResolve(methodCall)) {
                return;
            }
            ResolvedMethodDeclaration resolvedMethodCall = methodCall.resolve();
            edges.add(new MethodCallEdge(resolvedNode.getQualifiedName(), resolvedMethodCall.getQualifiedName()));
        });

        return edges;
    }

}
