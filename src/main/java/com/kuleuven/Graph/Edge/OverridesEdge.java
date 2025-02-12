package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Node.Node;

public class OverridesEdge extends Edge {


    public OverridesEdge(Node source, Node destination) {
        super(source, destination);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.OVERRIDES;
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
