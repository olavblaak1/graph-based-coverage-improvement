package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

public class FanInFanOutMetric implements MetricStrategy {

    /*
     * This metric calculates the fan-in and fan-out of a node. The fan-in of a node
     * is the number of edges that have the node as destination. The fan-out of a
     * node is the number of edges that have the node as source. The rank of a node
     * is the sum of the fan-in and fan-out.
     *
     * Currently, fan-in is weighted 0.2 and fan-out is weighted 0.8.
     * This is becaue fan-out has a more significant impact on bug-proneness than fan-in.
     */

    @Override
    public double calculateRank(Node node, Graph graph) {
        double fanIn = graph.getIncomingEdges(node).size();
        double fanOut = graph.getOutgoingEdges(node).size();
        return 0.2 * fanIn + 0.8 * fanOut;
    }

    // todo: maybe get rid of this, the difference between the ranks is very small and we are adding ranks together

    @Override
    public void normalizeGraph(RankedGraph<? extends Graph> rankedGraph) {

    }
}
