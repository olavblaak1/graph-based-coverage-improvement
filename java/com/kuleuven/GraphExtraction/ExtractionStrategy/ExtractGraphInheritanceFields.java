package com.kuleuven.GraphExtraction.ExtractionStrategy;

import java.util.LinkedList;
import java.util.List;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.kuleuven.GraphExtraction.NodeVisitors.ClassVisitor;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.FieldEdge;
import com.kuleuven.Graph.Edge.InheritanceEdge;

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
        List<Node> graphNodes = new LinkedList<>();
        nodes.forEach(node -> {
            String className = node.getFullyQualifiedName().orElse("Unknown");
            Node graphNode = new ClassNode(className);
            graphNodes.add(graphNode);
        });
        return graphNodes;
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
        List<ClassOrInterfaceDeclaration> nodes = new LinkedList<>();
        ClassVisitor classVisitor = new ClassVisitor();
        compilationUnits.forEach(cu -> {
            cu.accept(classVisitor, null);
        });
        nodes.addAll(classVisitor.getDeclaredClasses());
        return nodes;
    }


        /**
     * Extracts the edges from one class to another if the class holds this class as
     * a type in a field
     * 
     * @param classDefinition
     * @return
     */
    private List<Edge> extractFieldEdges(ClassOrInterfaceDeclaration classDefinition) {
        List<Edge> edges = new LinkedList<>();

        List<ResolvedReferenceType> referencedTypes = new LinkedList<>();
        classDefinition.getFields().forEach(field ->
                field.getVariables().forEach(variableDeclarator -> {
            ResolvedType type = variableDeclarator.resolve().getType();

            if (type.isArray()) {
                type = type.asArrayType().getComponentType();
            }
            if (type.isReferenceType()) {
                referencedTypes.addAll(ExtractGraphHelper.extractReferencedTypes(type.asReferenceType()));
            }
        }));

        String className = classDefinition.getFullyQualifiedName().orElse("Unknown");
        referencedTypes.forEach(referencedType -> {
            String referencedClassName = referencedType.describe();
            Edge edge = new FieldEdge(className, referencedClassName);
            edges.add(edge);
        });

        return edges;
    }

    private static List<Edge> extractInheritanceEdges(ClassOrInterfaceDeclaration classDefinition) {
        List<Edge> edges = new LinkedList<>();
        List<ResolvedReferenceType> inheritedClasses = new LinkedList<>();

        classDefinition.resolve().getAncestors().forEach(ancestor ->
                inheritedClasses.addAll(ExtractGraphHelper.extractReferencedTypes(ancestor)));

        inheritedClasses.forEach(inheritedClass -> {
            String extendedClassName = inheritedClass.describe();
            String className = classDefinition.getFullyQualifiedName().orElse("Unknown");
            Edge edge = new InheritanceEdge(className, extendedClassName);
            edges.add(edge);
        });
        return edges;
    }
    
    
}
