package com.kuleuven.Graph.Node;

import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.TestMinimization.ImportanceCalculation.GraphImportanceVisitor;

public class ClassNode extends Node {
    public ClassNode(String name) {
        super(name);
    }

    @Override
    public NodeType getType() {
        return NodeType.CLASS;
    }

    @Override
    public double accept(GraphImportanceVisitor visitor, RankedGraph<CoverageGraph> graph) {
        return visitor.calculateImportance(this, graph);
    }

    @Override
    public boolean accept(CoverageVisitor coverageVisitor, ResolvedMethodDeclaration methodDeclaration) {
        return coverageVisitor.isCoveredBy(this, methodDeclaration);
    }

    @Override
    public void accept(MarkVisitor visitor, CoverageGraph graph) {
        visitor.mark(this, graph);
    }

    @Override
    public boolean accept(CoverageVisitor coverageVisitor, ResolvedFieldDeclaration field) {
        return coverageVisitor.isCoveredBy(this, field);
    }
}
