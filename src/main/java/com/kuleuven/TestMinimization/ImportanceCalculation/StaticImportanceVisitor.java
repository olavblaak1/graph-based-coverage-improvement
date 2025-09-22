package com.kuleuven.TestMinimization.ImportanceCalculation;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

public class StaticImportanceVisitor extends GraphImportanceVisitor {

    private final double coverageFactor;

    public StaticImportanceVisitor(double discountFactor, double coverageFactor) {
        this.coverageFactor = coverageFactor;
        this.discountFactor = discountFactor;
    }

    @Override
    protected double getImportance(Edge edge, RankedGraph<CoverageGraph> graph) {
        return Math.exp(-graph.getGraph().getMarkedEdgeCount(edge));
    }

    @Override
    protected double getImportance(Node node, RankedGraph<CoverageGraph> graph) {
        // e^(-markedNodes) * rank
        return Math.exp(-graph.getGraph().getMarkedNodeCount(node)) /* +  Math.exp(graph.getRank(node))*/;
    }

}