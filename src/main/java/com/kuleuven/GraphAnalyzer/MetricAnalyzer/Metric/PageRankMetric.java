package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PageRankMetric implements MetricStrategy {

    private final Map<Node, Double> pageRankScores = new HashMap<>();
    private static final int MAX_ITERATIONS = 100;
    private static final double DAMPING_FACTOR = 0.85;
    private static final double TOLERANCE = 1.0e-6;

    @Override
    public void preprocess(Graph graph) {
        computePageRank(graph);
    }

    @Override
    public double calculateRank(Node node, Graph graph) {
        if (pageRankScores.isEmpty()) {
            computePageRank(graph);
        }
        return pageRankScores.getOrDefault(node, 0.0);
    }

    private void computePageRank(Graph graph) {
        Collection<Node> nodes = graph.getNodes();
        int n = nodes.size();

        Map<Node, Double> scores = new HashMap<>();
        for (Node node : nodes) {
            scores.put(node, 1.0 / n);
        }

        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            Map<Node, Double> newScores = new HashMap<>();
            double danglingScore = 0.0;

            // Step 1: Collect dangling node contributions (no outgoing edges)
            for (Node node : nodes) {
                if (graph.getOutgoingEdges(node).isEmpty()) {
                    danglingScore += scores.get(node);
                }
            }

            boolean converged = true;

            for (Node node : nodes) {
                double incomingSum = 0.0;
                for (Edge incoming : graph.getIncomingEdges(node)) {
                    Node incomingNode = incoming.getSource();
                    int outDegree = graph.getOutgoingEdges(incomingNode).size();
                    if (outDegree > 0) {
                        incomingSum += scores.get(incomingNode) / outDegree;
                    }
                }

                double rank = (1.0 - DAMPING_FACTOR) / n;
                rank += DAMPING_FACTOR * (incomingSum + (danglingScore / n));
                newScores.put(node, rank);

                if (Math.abs(rank - scores.get(node)) > TOLERANCE) {
                    converged = false;
                }
            }

            scores = newScores;

            if (converged) {
                break;
            }
        }

        pageRankScores.clear();
        pageRankScores.putAll(scores);
    }

    @Override
    public void normalizeGraph(RankedGraph<? extends Graph> rankedGraph) {
        // Optional: normalize scores here if needed
    }
}