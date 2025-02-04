package com.kuleuven.Graph.Edge;

public class InheritanceEdge extends Edge {

    public InheritanceEdge(String subclass, String superclass) {
        super(subclass, superclass);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.INHERITANCE;
    }
}