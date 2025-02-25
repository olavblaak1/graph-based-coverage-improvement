package com.kuleuven.TestMinimization;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

public class StandardTestCaseVisitor extends TestCaseVisitor {

    private double coverageFactor;

    StandardTestCaseVisitor(double discountFactor, double coverageFactor) {
        this.discountFactor = discountFactor;
        this.coverageFactor = coverageFactor;
    }

    @Override
    double getImportance(Edge edge, RankedGraph<CoverageGraph> graph) {
        return Math.exp(-graph.getGraph().getMarkedEdgeCount(edge)) *
                (getImportance(edge.getSource(), graph) + getImportance(edge.getDestination(), graph)) / 2 ;
    }

    @Override
    double getImportance(Node node, RankedGraph<CoverageGraph> graph) {
        // e^(-markedNodes) * rank
        return Math.exp(-graph.getGraph().getMarkedNodeCount(node)) * (coverageFactor + (1 - coverageFactor) * Math.exp(graph.getRank(node)));
    }
}