package com.kuleuven.CoverageAnalysis.EdgeAnalysis;

import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Node.Node;

public class CoverageManager {
    private final FieldCoverageChecker fieldCoverageVisitor;
    private final MethodCoverageChecker methodCoverageVisitor;

    public CoverageManager() {
        this.fieldCoverageVisitor = new FieldCoverageChecker();
        this.methodCoverageVisitor = new MethodCoverageChecker();
    }


    public boolean isCoveredBy(Edge edge, ResolvedMethodDeclaration method) {
        return edge.accept(methodCoverageVisitor, method);
    }

    public boolean isCoveredBy(Node node, ResolvedMethodDeclaration method) {
        return node.accept(methodCoverageVisitor, method);
    }

    public boolean isCoveredBy(Node node, ResolvedFieldDeclaration field) {
        return node.accept(fieldCoverageVisitor, field);
    }

    public boolean isCoveredBy(Edge edge, ResolvedFieldDeclaration field) {
        return edge.accept(fieldCoverageVisitor, field);
    }
}
