package com.kuleuven.GraphExtraction.ExtractionStrategy;

import java.util.LinkedList;
import java.util.List;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.kuleuven.GraphExtraction.ExtractionStrategy.NodeVisitors.ClassVisitor;
import com.kuleuven.GraphExtraction.Graph.NodeType;
import com.kuleuven.GraphExtraction.Graph.Node;
import com.kuleuven.GraphExtraction.Graph.Edge.Edge;

public class ExtractGraphInheritanceFields extends ExtractionTemplate {

    @Override
    public List<Edge> extractEdges(List<com.github.javaparser.ast.Node> nodes) {
        List<Edge> edges = new LinkedList<>();
        nodes.forEach(node -> {
            if (node instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classDefinition = (ClassOrInterfaceDeclaration) node;    
                edges.addAll(ExtractGraphHelper.extractUniqueMethodCallEdges(classDefinition));
                edges.addAll(ExtractGraphHelper.extractInheritanceEdges(classDefinition));
                edges.addAll(ExtractGraphHelper.extractFieldEdges(classDefinition));
            }
        });
        return edges;
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


    /**
     * Extracts the AST's nodes, which in this case are the CLASS DEFINITIONS
     * 
     * @param cu: the CompilationUnit of the Java source file
     * @return the list of AST nodes, which are the class definitions
     * 
     */
    @Override
    public List<com.github.javaparser.ast.Node> extractASTNodes(List<CompilationUnit> compilationUnits) {
        List<com.github.javaparser.ast.Node> nodes = new LinkedList<>();
        ClassVisitor classVisitor = new ClassVisitor();
        compilationUnits.forEach(cu -> {
            cu.accept(classVisitor, null);
        });
        nodes.addAll(classVisitor.getDeclaredClasses());
        return nodes;
    }
}
