package com.kuleuven.TestMinimization;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

public class StandardTestCaseVisitor extends TestCaseVisitor {

    @Override
    double getImportance(Edge edge, RankedGraph<CoverageGraph> graph) {
        return Math.exp(-graph.getGraph().getMarkedEdgeCount(edge)) *
                (getImportance(edge.getSource(), graph) + getImportance(edge.getDestination(), graph)) / 2 ;
    }

    @Override
    double getImportance(Node node, RankedGraph<CoverageGraph> graph) {
        // e^(-markedNodes) * rank
        return Math.exp(-graph.getGraph().getMarkedNodeCount(node)) * (0.3 + (1 - 0.3) * graph.getRank(node));
    }

}
