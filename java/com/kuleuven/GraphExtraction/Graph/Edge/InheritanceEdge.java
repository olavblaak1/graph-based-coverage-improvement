package com.kuleuven.GraphExtraction.Graph.Edge;

import com.kuleuven.GraphExtraction.Graph.Node;

public class InheritanceEdge extends Edge {

    public InheritanceEdge(Node subclass, Node superclass) {
        super(subclass, superclass);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.INHERITANCE;
    }
}