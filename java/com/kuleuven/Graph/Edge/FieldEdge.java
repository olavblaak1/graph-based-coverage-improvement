package com.kuleuven.Graph.Edge;

public class FieldEdge extends Edge {


    public FieldEdge(String source, String destination) {
        super(source, destination);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.FIELD;
    }
    
}
