package com.kuleuven.GraphExtraction.ExtractionStrategy.Template;

import java.util.LinkedList;
import java.util.List;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.kuleuven.GraphExtraction.ExtractionStrategy.ExtractGraphHelper;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Edge.Edge;


public class ExtractGraphInheritanceFields extends ExtractionTemplate<ClassOrInterfaceDeclaration> {

    @Override
    public List<Edge> extractEdges(List<ClassOrInterfaceDeclaration> nodes) {
        List<Edge> edges = new LinkedList<>();
        nodes.forEach(node -> {  
            edges.addAll(ExtractGraphHelper.extractMethodCallEdges(node));
            edges.addAll(extractInheritanceEdges(node));
            edges.addAll(extractFieldEdges(node));
        });
        return edges;
    }

    @Override
    public List<Node> convertNodes(List<ClassOrInterfaceDeclaration> nodes) {
        return ExtractGraphHelper.extractClassNodes(nodes);
    }


    /**
     * Extracts the AST's nodes, which in this case are the CLASS DEFINITIONS
     * 
     * @param compilationUnits: the CompilationUnits of the Java source file
     * @return the list of AST nodes, which are the class definitions
     * 
     */
    @Override
    public List<ClassOrInterfaceDeclaration> extractASTNodes(List<CompilationUnit> compilationUnits) {
        return ExtractGraphHelper.getClassesFromCompilationUnits(compilationUnits);
    }


    private List<Edge> extractFieldEdges(ClassOrInterfaceDeclaration classDefinition) {
        return ExtractGraphHelper.extractFieldEdges(classDefinition);
    }

    private static List<Edge> extractInheritanceEdges(ClassOrInterfaceDeclaration classDefinition) {
        return ExtractGraphHelper.extractInheritanceEdges((classDefinition));
    }

}
