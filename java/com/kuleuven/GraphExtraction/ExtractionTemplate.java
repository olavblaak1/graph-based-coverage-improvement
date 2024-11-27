package com.kuleuven.GraphExtraction;


import com.github.javaparser.ast.CompilationUnit;
import com.kuleuven.GraphExtraction.Graph.Edge.Edge;
import com.kuleuven.GraphExtraction.Graph.Node;

import java.util.List;



/*
 * Template for extracting the graph from a Java source file.
 */
public abstract class ExtractionTemplate {
    
    /**
     * Template method for extracting the edges and nodes from a Java source file.
     * 
     * @param file: the Java source file
     * @param edges: the edges of the graph
     * @param nodes: the nodes of the graph
     * @pre  edges contains the edges of the graph up until now
     * @pre  nodes contains the nodes of the graph up until now
     * @post edges contains the edges of the graph up until now and the edges extracted from the Java source file
     * @post nodes contains the nodes of the graph up until now and the nodes extracted from the Java source file
     */
    public void extractGraph(CompilationUnit cu, List<Edge> edges, List<Node> nodes) {
        // extracts all of the abstract syntax tree Nodes from the Java source file, 
        // these may be classes, methods, statements, ...
        List<com.github.javaparser.ast.Node> ASTNodes = extractASTNodes(cu);

        // Extracts the edges of the graph from the AST nodes
        // these may be method calls, inheritence relations, ...
        edges.addAll(extractEdges(ASTNodes));

        // Converts the AST nodes to the graph nodes
        nodes.addAll(convertNodes(ASTNodes));
    }

    /**
     * Extracts the edges of the graph from a Java source file.
     * @param nodes: the nodes of the graph
     * @return the edges of the graph
     */
    public abstract List<Edge> extractEdges(List<com.github.javaparser.ast.Node> nodes);


    /**
     * Extracts all JavaParser Abstract Syntax Tree nodes from the Java source file for further processing.
    */ 
    public abstract List<com.github.javaparser.ast.Node> extractASTNodes(CompilationUnit cu);


    /**
     * Converts the JavaParser AST nodes to the graph nodes.
     */
    public abstract List<Node> convertNodes(List<com.github.javaparser.ast.Node> nodes);
}