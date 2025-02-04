package com.kuleuven.Graph;

public class MethodNode extends Node {

    public enum OverWrite {
        YES, NO, UNKNOWN
    }

    private OverWrite overWrite; 

    public MethodNode(String name, OverWrite overWrite) {
        super(name);
        this.overWrite = overWrite;
    }

    @Override
    public NodeType getType() {
        return NodeType.METHOD;
    }

    public String getClassName() {
        String name = super.getName();
        int lastDotIndex = name.lastIndexOf('.');
        return lastDotIndex != -1 ? name.substring(0, lastDotIndex) : name;
    }

    public String getOverWrite() {
        return overWrite.toString();
    }
}
