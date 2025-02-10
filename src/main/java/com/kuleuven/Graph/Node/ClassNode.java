package com.kuleuven.Graph.Node;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class ClassNode extends Node {
    public ClassNode(String name) {
        super(name);
    }

    @Override
    public NodeType getType() {
        return NodeType.CLASS;
    }

    @Override
    public boolean isCoveredBy(ResolvedMethodDeclaration testMethod) {
        return testMethod.getClassName().equals(super.getName());
    }

}
