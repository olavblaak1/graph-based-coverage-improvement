package com.kuleuven.Graph;

public class MethodNode extends Node {
    private String superClass;

    public MethodNode(String name, String superClass) {
        super(name);
        this.superClass = superClass;
    }

    @Override
    public NodeType getType() {
        return NodeType.METHOD;
    }

    public String getSuperClassName() {
        return superClass;
    }
}
