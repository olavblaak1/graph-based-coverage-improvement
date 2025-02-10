package com.kuleuven.Graph.Node;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public abstract class Node {
    private final String name;

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract NodeType getType();

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
        return name.equals(node.getName());
    }

    public abstract boolean isCoveredBy(ResolvedMethodDeclaration testMethod);
}