package com.kuleuven.GraphExtraction.ExtractionStrategy.Template;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.GraphExtraction.ExtractionStrategy.ExtractGraphHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ExtractFullGraph extends ExtractionTemplate<com.github.javaparser.ast.Node> {


    @Override
    public Collection<Edge> extractEdges(List<com.github.javaparser.ast.Node> nodes) {
        LinkedList<Edge> edges = new LinkedList<>();
        getClassOrInterfaceDeclarations(nodes).forEach(node -> {
            // edges.addAll(ExtractGraphHelper.extractMethodCallEdges(node)); now encapsulated by method declarations
            edges.addAll(ExtractGraphHelper.extractInheritanceEdges(node));
            edges.addAll(ExtractGraphHelper.extractFieldEdges(node));
        });

        getMethodDeclarations(nodes).forEach(node ->
                edges.addAll(ExtractGraphHelper.extractMethodCallEdges(node)));

        nodes.forEach(node ->
                ExtractGraphHelper.extractOwnsMethodEdge(node).ifPresent(edges::add));
        return edges;
    }

    @Override
    public List<com.github.javaparser.ast.Node> extractASTNodes(List<CompilationUnit> compilationUnits) {
        List<ClassOrInterfaceDeclaration> classes = ExtractGraphHelper.getClassesFromCompilationUnits(compilationUnits);
        List<MethodDeclaration> methods = ExtractGraphHelper.extractMethodsFromCompilationUnits(compilationUnits);

        List<com.github.javaparser.ast.Node> nodes = new ArrayList<>(classes.size() + methods.size());
        nodes.addAll(classes);
        nodes.addAll(methods);
        return nodes;
    }

    @Override
    protected List<Node> convertNodes(List<com.github.javaparser.ast.Node> nodes) {
        List<Node> classNodes = ExtractGraphHelper.extractClassNodes(getClassOrInterfaceDeclarations(nodes));
        List<Node> methodNodes = ExtractGraphHelper.extractMethodNodes(getMethodDeclarations(nodes));

        List<Node> resultNodes = new ArrayList<>(classNodes.size() + methodNodes.size());
        resultNodes.addAll(classNodes);
        resultNodes.addAll(methodNodes);
        return resultNodes;
    }

    private List<ClassOrInterfaceDeclaration> getClassOrInterfaceDeclarations(List<com.github.javaparser.ast.Node> nodes) {
        return nodes.stream()
                .filter(e -> (e instanceof ClassOrInterfaceDeclaration))
                .map(e -> (ClassOrInterfaceDeclaration) e)
                .collect(Collectors.toList());
    }

    private List<MethodDeclaration> getMethodDeclarations(List<com.github.javaparser.ast.Node> nodes) {
        return nodes.stream()
                .filter(e -> (e instanceof MethodDeclaration))
                .map(e -> (MethodDeclaration) e)
                .collect(Collectors.toList());
    }
}
