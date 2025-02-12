package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.Node;
import org.checkerframework.checker.units.qual.C;

public class FieldEdge extends Edge {


    public FieldEdge(Node source, Node destination) {
        super(source, destination);
    }

    public FieldEdge(String source, String destination) {
        super(new ClassNode(source), new ClassNode(destination));
    }

    @Override
    public EdgeType getType() {
        return EdgeType.FIELD;
    }

    @Override
    public boolean accept(CoverageVisitor visitor, ResolvedMethodDeclaration methodDeclaration) {
        return visitor.isCoveredBy(this, methodDeclaration);
    }
}
