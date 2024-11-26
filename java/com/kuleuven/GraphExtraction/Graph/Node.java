package com.kuleuven.GraphExtraction.Graph;

public class Node {
    private String name;
    private NodeType type;

    public Node(String name, NodeType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public NodeType getType() {
        return type;
    }
}