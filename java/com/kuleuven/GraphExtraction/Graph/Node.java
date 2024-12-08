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

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        Node node = (Node) obj;
        return name.equals(node.getName()) && type.equals(node.getType());
    }
}