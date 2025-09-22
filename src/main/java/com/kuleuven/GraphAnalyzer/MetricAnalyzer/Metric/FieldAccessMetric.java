package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.NodeType;

public class FieldAccessMetric implements MetricStrategy {
    @Override
    public void preprocess(Graph graph) {
        // No preprocessing needed for this metric
    }

    @Override
    public double calculateRank(Node node, Graph graph) {
        // Calculate the number of field accesses for the given node
        if (node.getType().equals(NodeType.METHOD)) {
            return graph.getOutgoingEdges(node).stream()
                    .filter(edge -> edge.getType().equals(EdgeType.FIELD_ACCESS))
                    .count();
        }
        return 0;
    }

    @Override
    public void normalizeGraph(RankedGraph<? extends Graph> rankedGraph) {
        // No normalization needed for this metric
    }
}
