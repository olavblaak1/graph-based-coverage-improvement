package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.NodeType;

public class OverridesMetric implements MetricStrategy {

    @Override
    public void preprocess(Graph graph) {
        return;
    }

    @Override
    public double calculateRank(Node node, Graph graph) {
        if (node.getType().equals(NodeType.METHOD)) {
            // Check if the method overrides another method
            boolean overridesMethod = !graph.getOutgoingEdgesOfType(node, EdgeType.OVERRIDES).isEmpty();
            return overridesMethod ? 1.0 : 0.0;
        }
        return 0.0;
    }

    @Override
    public void normalizeGraph(RankedGraph<? extends Graph> rankedGraph) {
        return;
    }
}