package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class InheritanceEdge extends Edge {

    public InheritanceEdge(String subclass, String superclass) {
        super(subclass, superclass);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.INHERITANCE;
    }

    @Override
    public boolean isCoveredBy(ResolvedMethodDeclaration methodDeclaration) {
        return false;
        // TODO: Implement (figure out when this type of edge is covered)
    }
}