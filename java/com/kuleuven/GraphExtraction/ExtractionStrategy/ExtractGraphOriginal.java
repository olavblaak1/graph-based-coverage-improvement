package com.kuleuven.GraphExtraction.ExtractionStrategy;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.kuleuven.GraphExtraction.Graph.Node;
import com.kuleuven.GraphExtraction.Graph.Edge.Edge;
import com.kuleuven.GraphExtraction.ExtractionStrategy.NodeVisitors.ClassVisitor;
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
    public Set<Edge> extractEdges(List<com.github.javaparser.ast.Node> nodes) {
        Set<Edge> edges = new HashSet<>();
        nodes.forEach(node -> {
        if (node instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration classDefinition = (ClassOrInterfaceDeclaration) node;
            edges.addAll(ExtractGraphHelper.extractUniqueMethodCallEdges(classDefinition));
        }
        });
        return edges;
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
