package com.kuleuven.GraphExtraction.ExtractionStrategy.Template;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.GraphExtraction.ExtractionStrategy.ExtractGraphHelper;
import com.kuleuven.Graph.Node.isOverride;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ExtractMethodGraph extends ExtractionTemplate<MethodDeclaration> {

    @Override
    public Collection<Edge> extractEdges(List<MethodDeclaration> nodes) {
        List<Edge> edges = new java.util.LinkedList<>();
        nodes.forEach(node -> edges.addAll(extractMethodCallEdges(node)));
        return edges;
    }

    @Override
    public List<MethodDeclaration> extractASTNodes(List<CompilationUnit> compilationUnits) {
        return ExtractGraphHelper.extractMethodsFromCompilationUnits(compilationUnits);
    }

    @Override
    protected List<Node> convertNodes(List<MethodDeclaration> nodes) {
        List<Node> graphNodes = new LinkedList<>();
        nodes.forEach(node -> {
            isOverride override = node.getAnnotationByName("Override").isPresent() ? isOverride.YES : isOverride.NO;
            ResolvedMethodDeclaration resolvedNode = node.resolve();
            String name = resolvedNode.getQualifiedName();
            String signature = resolvedNode.getSignature();
            graphNodes.add(new MethodNode(name, override, signature));
        });
        return graphNodes;
    }


    private List<Edge> extractMethodCallEdges(MethodDeclaration node) {
        return ExtractGraphHelper.extractMethodCallEdges(node);
    }

}
