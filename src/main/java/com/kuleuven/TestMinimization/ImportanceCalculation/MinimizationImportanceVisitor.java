package com.kuleuven.TestMinimization.ImportanceCalculation;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

public class MinimizationImportanceVisitor extends GraphImportanceVisitor {

    private final double coverageFactor;

    public MinimizationImportanceVisitor(double discountFactor, double coverageFactor) {
        this.discountFactor = discountFactor;
        this.coverageFactor = coverageFactor;
    }

    @Override
    protected double getImportance(Edge edge, RankedGraph<CoverageGraph> graph) {
        return Math.exp(-graph.getGraph().getMarkedEdgeCount(edge)) *
                (getImportance(edge.getSource(), graph) + getImportance(edge.getDestination(), graph)) / 2;
    }

    @Override
    protected double getImportance(Node node, RankedGraph<CoverageGraph> graph) {
        // e^(-markedNodes) * rank
        return Math.exp(-graph.getGraph().getMarkedNodeCount(node)) * (coverageFactor + (1 - coverageFactor) * graph.getRank(node));
    }

    private double normalizeNodeExponent(double value, RankedGraph<CoverageGraph> graph) {
        return value / (graph.getMaxRank() + graph.getGraph().getMaxNodeCoverCount());
    }
}