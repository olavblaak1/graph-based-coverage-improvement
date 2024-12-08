package com.kuleuven.GraphExtraction.Graph;

public class MethodNode extends Node {
    public MethodNode(String name) {
        super(name);
    }

    @Override
    public NodeType getType() {
        return NodeType.METHOD;
    }
    
}
