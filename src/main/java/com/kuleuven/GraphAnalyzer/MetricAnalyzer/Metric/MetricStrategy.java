package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.RankedNode;

import java.util.List;


public interface MetricStrategy {


    default <T extends Graph> RankedGraph<T> calculateMetric(T graph) {
        RankedGraph<T> rankedGraph = new RankedGraph<T>(graph);
        graph.getNodes().forEach(node -> {
            double rank = calculateRank(node, graph);
            rankedGraph.setRank(node, rank);
        });
        return rankedGraph;
    }

    double calculateRank(Node node, Graph graph);
}
