package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.NodeType;

public class NOOMMetric implements MetricStrategy {

    /*
     * NOOM = Σ (number of methods overridden by a class)
     * it was found this correlates to bug proneness
     */
    @Override
    public double calculateRank(Node node, Graph graph) {
        double noom = 0;
        if (node.getType().equals(NodeType.CLASS)) {
            // Iterate over all methods of the class, and check if they override a method
            noom += graph.getIncomingEdgesOfType(node, EdgeType.OWNED_BY).stream().filter(
                    edge -> !graph.getOutgoingEdgesOfType(edge.getSource(), EdgeType.OVERRIDES).isEmpty()
            ).count();
        }
        return noom;
    }
}
