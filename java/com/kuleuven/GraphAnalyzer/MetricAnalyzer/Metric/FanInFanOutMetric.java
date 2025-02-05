package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import java.util.LinkedList;
import java.util.List;

import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.RankedNode;
import com.kuleuven.Graph.Edge.Edge;

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
    public List<RankedNode> calculateMetric(List<Node> nodes, List<Edge> edges) {
        List<RankedNode> rankedNodes = new LinkedList<RankedNode>();
        for (Node node : nodes) {
            float fanIn = 0;
            float fanOut = 0;
            for (Edge edge : edges) {
                if (edge.getDestination().equals(node)) {
                    fanIn++;
                }
                if (edge.getSource().equals(node)) {
                    fanOut++;
                }
            }
            double rank = 0.2 * fanIn + 0.8 * fanOut;
            rankedNodes.add(new RankedNode(node, rank));
        }
        return rankedNodes;
    }
}
