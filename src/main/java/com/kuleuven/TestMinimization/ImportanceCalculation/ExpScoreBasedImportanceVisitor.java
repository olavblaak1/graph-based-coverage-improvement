package com.kuleuven.TestMinimization.ImportanceCalculation;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

public class ExpScoreBasedImportanceVisitor extends GraphImportanceVisitor{
    @Override
    protected double getImportance(Edge edge, RankedGraph<CoverageGraph> graph) {
        return 0;
    }

    @Override
    protected double getImportance(Node node, RankedGraph<CoverageGraph> graph) {
        return Math.exp(graph.getRank(node));
    }
}
