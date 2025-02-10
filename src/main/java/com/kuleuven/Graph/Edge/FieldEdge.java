package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class FieldEdge extends Edge {


    public FieldEdge(String source, String destination) {
        super(source, destination);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.FIELD;
    }

    @Override
    public boolean isCoveredBy(ResolvedMethodDeclaration methodDeclaration) {
        return false;
        // TODO: implement (figure out when this type of edge is covered)
    }

}
