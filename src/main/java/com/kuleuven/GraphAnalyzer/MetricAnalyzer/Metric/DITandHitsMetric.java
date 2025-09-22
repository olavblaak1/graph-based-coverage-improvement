package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

public class DITandHitsMetric implements MetricStrategy {

    MetricStrategy metricStrategy = new HITSMetric();
    /*
     * This metric calculates the DIT and HITS of a node. The DIT of a node is the
     * depth of the inheritance tree of the node. The HITS of a node is the number
     * of incoming edges to the node. The rank of a node is the sum of the DIT and
     * HITS.
     *
     * Currently, DIT is weighted 0.2 and HITS is weighted 0.8.
     * This is becaue HITS has a more significant impact on bug-proneness than DIT.
     */

    @Override
    public void preprocess(Graph graph) {
        metricStrategy.preprocess(graph);
    }

    @Override
    public double calculateRank(Node node, Graph graph) {
        double dit = new DITMetric().calculateRank(node, graph);
        double hitsvalue = metricStrategy.calculateRank(node, graph);
        System.out.println(hitsvalue);
        return dit + Math.exp(hitsvalue);
    }

    @Override
    public void normalizeGraph(RankedGraph<? extends Graph> rankedGraph) {
        // No normalization needed for this metric
    }
}
