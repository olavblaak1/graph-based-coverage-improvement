package com.kuleuven.GraphExtraction.ExtractionStrategy.Template;


import com.github.javaparser.ast.CompilationUnit;
import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Edge.Edge;

import java.util.Collection;
import java.util.List;



/*
 * Template for extracting the graph from a Java source file using the JavaParser library.
 */
public abstract class ExtractionTemplate<T extends com.github.javaparser.ast.Node> {
    
    /**
     * Template method for extracting the edges and nodes from a Java source file.
     * 
     * @param compilationUnits: the CompilationUnits representing the Java source file
     * @param graph : the graph representation
     * @pre  graph contains the edges and nodes up until now
     * @post graph contains the nodes and edges up until now and the nodes extracted from the CompilationUnits
     */
    public void extractGraph(List<CompilationUnit> compilationUnits, Graph graph) {
        // extracts all the abstract syntax tree Nodes from the Java source file,
        // these may be classes, methods, statements, ...
        List<T> ASTNodes = extractASTNodes(compilationUnits);

        // Converts the AST nodes to graph nodes
        for (Node node : convertNodes(ASTNodes)) {
            graph.addNode(node);
        }


        // Extracts the edges of the graph from the AST nodes
        // these may be method calls, inheritance relations, ...
        for (Edge edge : extractEdges(ASTNodes)) {
            graph.addEdge(edge);
        }

    }

    /**
     * Extracts the edges of the graph from a Java source file.
     * @param nodes: the nodes of the graph
     * @return the edges of the graph
     */
    public abstract Collection<Edge> extractEdges(List<T> nodes);


    /**
     * Extracts all JavaParser Abstract Syntax Tree nodes from the Java source file for further processing.
    */ 
    public abstract List<T> extractASTNodes(List<CompilationUnit> compilationUnits);


    /**
     * Converts the JavaParser AST nodes to the graph nodes.
     */
    protected abstract List<Node> convertNodes(List<T> nodes);

}