package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.Coverage;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
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
    public boolean accept(CoverageVisitor visitor, ResolvedMethodDeclaration methodDeclaration) {
        return visitor.isCoveredBy(this, methodDeclaration);
    }
}