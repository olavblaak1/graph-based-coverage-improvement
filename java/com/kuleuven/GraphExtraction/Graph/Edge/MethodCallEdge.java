package com.kuleuven.GraphExtraction.Graph.Edge;

import com.kuleuven.GraphExtraction.Graph.Node;

public class MethodCallEdge extends Edge {


    public MethodCallEdge(Node source, Node destination) { 
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