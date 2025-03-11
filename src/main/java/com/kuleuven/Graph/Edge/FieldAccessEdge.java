package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.TestMinimization.ImportanceCalculation.GraphImportanceVisitor;

public class FieldAccessEdge extends Edge {
    public FieldAccessEdge(Node source, Node destination) {
        super(source, destination);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.FIELD_ACCESS;
    }

    @Override
    public boolean accept(CoverageVisitor<ResolvedMethodDeclaration> visitor, ResolvedMethodDeclaration methodDeclaration) {
        return visitor.isCoveredBy(this, methodDeclaration);
    }

    @Override
    public boolean accept(CoverageVisitor<ResolvedFieldDeclaration> visitor, ResolvedFieldDeclaration fieldDeclaration) {
        return visitor.isCoveredBy(this, fieldDeclaration);
    }

    @Override
    public double accept(GraphImportanceVisitor visitor, RankedGraph<CoverageGraph> graph) {
        return visitor.calculateImportance(this, graph);
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
