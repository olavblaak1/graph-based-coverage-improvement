package com.kuleuven.Graph.Node;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;

public class MethodNode extends Node {

    private final isOverride isOverride;
    private final String signature;

    public MethodNode(String name, isOverride isOverride, String signature) {
        super(name);
        this.signature = signature;
        this.isOverride = isOverride;
    }

    public MethodNode(String name, String signature) {
        super(name);
        this.signature = signature;
        this.isOverride = com.kuleuven.Graph.Node.isOverride.UNKNOWN;
    }

    @Override
    public String getId() {
        return super.getName() + ":" + signature;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public NodeType getType() {
        return NodeType.METHOD;
    }

    @Override
    public boolean accept(CoverageVisitor visitor, ResolvedMethodDeclaration methodDeclaration) {
        return visitor.isCoveredBy(this, methodDeclaration);
    }

    public String getMethodName() {
        // Todo: This is a language-dependent implementation, perhaps add declaring class to graph instead
        String name = super.getName();
        int lastDotIndex = name.lastIndexOf('.');
        return lastDotIndex != -1 ? name.substring(lastDotIndex + 1) : name;
    }

    public String getSignature() {
        return signature;
    }

    public String getClassName() {
        // Todo: This is a language-dependent implementation, perhaps add declaring class to graph instead
        String name = super.getName();
        int lastDotIndex = name.lastIndexOf('.');
        return lastDotIndex != -1 ? name.substring(0, lastDotIndex) : name;
    }

    public String isOverride() {
        return isOverride.toString();
    }
}

