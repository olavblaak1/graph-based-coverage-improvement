package com.kuleuven.GraphExtraction.Graph.Edge;

import com.kuleuven.GraphExtraction.Graph.Node;

public class FieldEdge extends Edge {


    public FieldEdge(Node source, Node destination) {
        super(source, destination);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.FIELD;
    }
    
}
