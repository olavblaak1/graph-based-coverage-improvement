package com.kuleuven.Graph.Node;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class MethodNode extends Node {

    private final OverWrite overWrite;

    public MethodNode(String name, OverWrite overWrite) {
        super(name);
        this.overWrite = overWrite;
    }

    @Override
    public NodeType getType() {
        return NodeType.METHOD;
    }

    @Override
    public boolean isCoveredBy(ResolvedMethodDeclaration testMethod) {
        return testMethod.getQualifiedName().equals(super.getName());
    }

    public String getMethodName() {
        String name = super.getName();
        int lastDotIndex = name.lastIndexOf('.');
        return lastDotIndex != -1 ? name.substring(lastDotIndex + 1) : name;
    }

    public String getClassName() {
        String name = super.getName();
        int lastDotIndex = name.lastIndexOf('.');
        return lastDotIndex != -1 ? name.substring(0, lastDotIndex) : name;
    }

    public String getOverWrite() {
        return overWrite.toString();
    }

    public enum OverWrite {
        YES, NO, UNKNOWN
    }
}
