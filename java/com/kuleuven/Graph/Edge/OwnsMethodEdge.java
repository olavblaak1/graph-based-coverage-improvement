package com.kuleuven.Graph.Edge;

public class OwnsMethodEdge extends Edge {

    public OwnsMethodEdge(String source, String destination) {
        super(source, destination);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.METHOD_OWN;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
