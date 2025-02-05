package com.kuleuven.GraphExtraction.ExtractionStrategy.Template;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.kuleuven.GraphExtraction.ExtractionStrategy.ExtractGraphHelper;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Edge.Edge;



/**
 * Original Graph Extraction method from Charles Sys' Thesis
 * 
 * With this strategy, the nodes of the resulting graph are CLASS DEFINITIONS
 * and the edges are METHOD CALLS between these classes, ignoring method calls within a class.
 * This one includes edges with imported classes, but not as nodes.
 */
public class ExtractGraphOriginal extends ExtractionTemplate<ClassOrInterfaceDeclaration> {

    /**
     * Extracts the edges of the graph from the list of Nodes
     * 
     * @param nodes: the list of Nodes to extract the edges from
     * @return the edges of the graph, which are the method calls between classes, ignoring method calls within a class
     */
    @Override
    public Set<Edge> extractEdges(List<ClassOrInterfaceDeclaration> nodes) {
        Set<Edge> edges = new HashSet<>();
        nodes.forEach(node -> {
            edges.addAll(ExtractGraphHelper.extractMethodCallEdges(node));
        });
        return edges;
    }

    /**
     * Extracts the AST's nodes, which in this case are the CLASS DEFINITIONS
     * 
     * @param compilationUnits: the CompilationUnit of the Java source file
     * @return the list of AST nodes, which are the class definitions
     * 
     */
    @Override
    public List<ClassOrInterfaceDeclaration> extractASTNodes(List<CompilationUnit> compilationUnits) {
        return ExtractGraphHelper.getClassesFromCompilationUnits(compilationUnits);
    }

    @Override
    public List<Node> convertNodes(List<ClassOrInterfaceDeclaration> nodes) {
        return ExtractGraphHelper.extractClassNodes(nodes);
    }
}
