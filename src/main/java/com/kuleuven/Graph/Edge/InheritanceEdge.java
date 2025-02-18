package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.Node;

public class InheritanceEdge extends Edge {

    public InheritanceEdge(Node subclass, Node superclass) {
        super(subclass, superclass);
    }

    public InheritanceEdge(String subclassName, String superclassName) {
        super(new ClassNode(subclassName), new ClassNode(superclassName));
    }

    @Override
    public EdgeType getType() {
        return EdgeType.INHERITANCE;
    }

    @Override
    public boolean accept(CoverageVisitor<ResolvedMethodDeclaration> visitor, ResolvedMethodDeclaration methodDeclaration) {
        return visitor.isCoveredBy(this, methodDeclaration);
    }

    @Override
    public boolean accept(CoverageVisitor<ResolvedFieldDeclaration> visitor, ResolvedFieldDeclaration fieldDeclaration) {
        return visitor.isCoveredBy(this, fieldDeclaration);
    }

    @Override
    public void accept(MarkVisitor visitor, CoverageGraph graph) {
        visitor.mark(this, graph);
    }
}