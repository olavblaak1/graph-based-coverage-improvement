package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

import java.util.HashMap;
import java.util.Map;

public class HITSMetric implements MetricStrategy {

    private static final int MAX_ITERATIONS = 100;
    private static final double TOLERANCE = 1e-6;

    private Map<Node, Double> authorityScores = new HashMap<>();
    private Map<Node, Double> hubScores = new HashMap<>();


    @Override
    public void preprocess(Graph graph) {
        runHITS(graph);
    }

    @Override
    public double calculateRank(Node node, Graph graph) {
        double authority = authorityScores.getOrDefault(node, 0.0);
        double hub = hubScores.getOrDefault(node, 0.0);
        return authority + hub; // or just return one of them
    }

    private void runHITS(Graph graph) {
        // Initialize scores
        for (Node node : graph.getNodes()) {
            authorityScores.put(node, 1.0);
            hubScores.put(node, 1.0);
        }

        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            System.out.println("Iteration " + iter + ": " + authorityScores.size() + " " + hubScores.size());
            Map<Node, Double> newAuthority = new HashMap<>();
            Map<Node, Double> newHub = new HashMap<>();

            // Update authority scores
            for (Node node : graph.getNodes()) {
                double score = 0.0;
                for (Edge in : graph.getIncomingEdges(node)) {
                    Node inNode = in.getSource();
                    score += hubScores.getOrDefault(inNode, 0.0);
                }
                newAuthority.put(node, score);
            }

            // Update hub scores
            for (Node node : graph.getNodes()) {
                double score = 0.0;
                for (Edge out : graph.getOutgoingEdges(node)) {
                    Node outNode = out.getDestination();
                    score += newAuthority.getOrDefault(outNode, 0.0);
                }
                newHub.put(node, score);
            }

            // Normalize
            normalize(newAuthority);
            normalize(newHub);

            // Check convergence
            if (hasConverged(authorityScores, newAuthority) && hasConverged(hubScores, newHub)) {
                break;
            }

            authorityScores = newAuthority;
            hubScores = newHub;
        }
    }

    private void normalize(Map<Node, Double> scores) {
        double norm = 0.0;
        for (double val : scores.values()) {
            norm += val * val;
        }
        norm = Math.sqrt(norm);
        if (norm == 0.0) return;
        for (Map.Entry<Node, Double> entry : scores.entrySet()) {
            entry.setValue(entry.getValue() / norm);
        }
    }

    private boolean hasConverged(Map<Node, Double> oldScores, Map<Node, Double> newScores) {
        for (Node node : oldScores.keySet()) {
            double oldVal = oldScores.getOrDefault(node, 0.0);
            double newVal = newScores.getOrDefault(node, 0.0);
            if (Math.abs(oldVal - newVal) > TOLERANCE) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void normalizeGraph(RankedGraph<? extends Graph> rankedGraph) {
        // If needed, you can put final scores into rankedGraph
    }
}