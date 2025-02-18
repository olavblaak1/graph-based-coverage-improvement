package com.kuleuven.Graph.Node;

import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.Graph.CoverageGraph;

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
        return node.hashCode() == this.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getId() {
        return name;
    }

    public abstract boolean accept(CoverageVisitor<ResolvedMethodDeclaration> nodeCoverageVisitor, ResolvedMethodDeclaration methodDeclaration);

    public abstract void accept(MarkVisitor nodeMarkVisitor, CoverageGraph graph);

    public abstract boolean accept(CoverageVisitor<ResolvedFieldDeclaration> coverageVisitor, ResolvedFieldDeclaration field);
}