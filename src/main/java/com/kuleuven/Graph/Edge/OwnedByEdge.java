package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.Node;

public class OwnedByEdge extends Edge {

    public OwnedByEdge(Node source, Node destination) {
        super(source, destination);
    }

    public OwnedByEdge(String source, String destination) {
        super(new ClassNode(source), new ClassNode(destination));
    }

    @Override
    public EdgeType getType() {
        return EdgeType.OWNED_BY;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean accept(CoverageVisitor<ResolvedFieldDeclaration> visitor, ResolvedFieldDeclaration fieldDeclaration) {
        return visitor.isCoveredBy(this, fieldDeclaration);
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
    public int getWeight() {
        return 1;
    }
}
