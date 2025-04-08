package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;


public interface MetricStrategy {


    // Calculate the rank of a node in a graph, should be greater than zero for untested path discovery to work
    default <T extends Graph> RankedGraph<T> calculateMetric(T graph) {
        RankedGraph<T> rankedGraph = new RankedGraph<>(graph);
        preprocess(graph);
        graph.getNodes().forEach(node -> {
            double rank = calculateRank(node, graph);
            rankedGraph.setRank(node, rank);
        });
        normalizeGraph(rankedGraph);
        return rankedGraph;
    }

    void preprocess(Graph graph);
    double calculateRank(Node node, Graph graph);
    void normalizeGraph(RankedGraph<? extends Graph> rankedGraph);
}
