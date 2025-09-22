package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.NodeType;

public class DITMetric implements MetricStrategy {

    @Override
    public void preprocess(Graph graph) {
        return;
    }

    @Override
    public double calculateRank(Node node, Graph graph) {
        if (node.getType() == NodeType.METHOD &&
                graph.getOutgoingEdges(node).stream().anyMatch(outgoing -> outgoing.getType().equals(EdgeType.OVERRIDES))) {
            Node overridingNode = graph.getOutgoingEdges(node).stream()
                    .filter(outgoing -> outgoing.getType().equals(EdgeType.OVERRIDES))
                    .findFirst()
                    .get()
                    .getDestination();
            return 1 + calculateRank(overridingNode, graph);
        }
        return 0;
    }

    @Override
    public void normalizeGraph(RankedGraph<? extends Graph> rankedGraph) {

    }
}
