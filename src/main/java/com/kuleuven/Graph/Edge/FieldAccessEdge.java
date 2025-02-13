package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Node.Node;

public class FieldAccessEdge extends Edge {
    public FieldAccessEdge(Node source, Node destination) {
        super(source, destination);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.FIELD_ACCESS;
    }

    @Override
    public boolean accept(CoverageVisitor visitor, ResolvedMethodDeclaration methodDeclaration) {
        return visitor.isCoveredBy(this, methodDeclaration);
    }

    @Override
    public void accept(MarkVisitor visitor, CoverageGraph graph) {
        visitor.mark(this, graph);
    }
}
