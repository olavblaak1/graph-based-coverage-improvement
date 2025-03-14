package com.kuleuven.MissingTestIdentification;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.TestMinimization.ImportanceCalculation.GraphImportanceVisitor;

public class MissingTestImportanceVisitor extends GraphImportanceVisitor {

    @Override
    protected double getImportance(Edge edge, RankedGraph<CoverageGraph> graph) {
        return getImportance(edge.getDestination(), graph);
    }

    @Override
    protected double getImportance(Node node, RankedGraph<CoverageGraph> graph) {
        return graph.getRank(node);
    }


    private double normalizeNodeExponent(double value, RankedGraph<CoverageGraph> graph) {
        return value / (graph.getMaxRank() + graph.getGraph().getMaxNodeCoverCount());
    }
}
