package com.kuleuven.GraphExtraction.Graph.Edge;

import com.kuleuven.GraphExtraction.Graph.Node;

public class MethodCallEdge extends Edge {

    private Method linkMethod;
    private Method sourceMethod;

    public MethodCallEdge(Node source, Node destination, Method linkMethod, Method sourceMethod) { 
        super(source, destination);
        this.linkMethod = linkMethod;
        this.sourceMethod = sourceMethod;
    }

    public Method getLinkMethod() {
        return linkMethod;
    }

    public Method getSourceMethod() {
        return sourceMethod;
    }

    @Override
    public EdgeType getType() {
        return EdgeType.METHODCALL;
    }
}