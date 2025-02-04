package com.kuleuven.Graph.Edge;

public class MethodCallEdge extends Edge {


    public MethodCallEdge(String source, String destination) {
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
}