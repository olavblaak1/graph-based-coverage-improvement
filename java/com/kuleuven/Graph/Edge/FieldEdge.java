package com.kuleuven.Graph.Edge;

import com.kuleuven.Graph.Node;

public class FieldEdge extends Edge {


    public FieldEdge(Node source, Node destination) {
        super(source, destination);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.FIELD;
    }
    
}
