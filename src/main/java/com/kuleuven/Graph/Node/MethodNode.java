package com.kuleuven.Graph.Node;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.Graph.CoverageGraph;

public class MethodNode extends Node {

    private final isOverride isOverride;
    private final String overriddenMethodID;
    private final String signature;

    public MethodNode(String name, isOverride isOverride, String signature, String overriddenMethodID) {
        super(name);
        this.signature = signature;
        this.isOverride = isOverride;
        this.overriddenMethodID = overriddenMethodID;
    }

    public MethodNode(String name, String signature) {
        super(name);
        this.signature = signature;
        this.isOverride = com.kuleuven.Graph.Node.isOverride.UNKNOWN;
        this.overriddenMethodID = "none";
    }

    public MethodNode(String name, String signature, isOverride isOverride) {
        super(name);
        this.signature = signature;
        this.isOverride = isOverride;
        this.overriddenMethodID = "none";
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
    public boolean accept(CoverageVisitor<ResolvedMethodDeclaration> visitor, ResolvedMethodDeclaration methodDeclaration) {
        return visitor.isCoveredBy(this, methodDeclaration);
    }

    @Override
    public void accept(MarkVisitor visitor, CoverageGraph graph) {
        visitor.mark(this, graph);
    }

    @Override
    public boolean accept(CoverageVisitor<ResolvedFieldDeclaration> coverageVisitor, ResolvedFieldDeclaration field) {
        return coverageVisitor.isCoveredBy(this, field);
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

    public String getOverriddenMethodID() {
        return overriddenMethodID;
    }

    public String getClassName() {
        // Todo: This is a language-dependent implementation, perhaps add declaring class to graph instead
        String name = super.getName();
        int lastDotIndex = name.lastIndexOf('.');
        return lastDotIndex != -1 ? name.substring(0, lastDotIndex) : name;
    }


    public boolean isOverride() {
        return isOverride.equals(com.kuleuven.Graph.Node.isOverride.YES);
    }
}

