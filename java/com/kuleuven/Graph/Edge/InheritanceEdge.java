package com.kuleuven.Graph.Edge;

import com.kuleuven.Graph.Node;

public class InheritanceEdge extends Edge {

    public InheritanceEdge(Node subclass, Node superclass) {
        super(subclass, superclass);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.INHERITANCE;
    }
}