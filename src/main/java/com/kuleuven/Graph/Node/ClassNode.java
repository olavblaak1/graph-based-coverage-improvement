package com.kuleuven.Graph.Node;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;

public class ClassNode extends Node {
    public ClassNode(String name) {
        super(name);
    }

    @Override
    public NodeType getType() {
        return NodeType.CLASS;
    }

    @Override
    public boolean accept(CoverageVisitor coverageVisitor, ResolvedMethodDeclaration methodDeclaration) {
        return coverageVisitor.isCoveredBy(this, methodDeclaration);
    }
}
