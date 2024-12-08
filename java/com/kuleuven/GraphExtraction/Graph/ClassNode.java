package com.kuleuven.GraphExtraction.Graph;

public class ClassNode extends Node {
    public ClassNode(String name) {
        super(name);
    }

    @Override
    public NodeType getType() {
        return NodeType.CLASS;
    }
    
}
