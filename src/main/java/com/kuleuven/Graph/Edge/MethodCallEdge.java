package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.Graph.Node.Node;

public class MethodCallEdge extends Edge {


    public MethodCallEdge(Node source, Node destination) {
        super(source, destination);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.METHOD_CALL;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean accept(CoverageVisitor visitor, ResolvedMethodDeclaration methodDeclaration) {
        return visitor.isCoveredBy(this, methodDeclaration);
    }
}