package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

import java.util.*;

public class BetweennessMetric implements MetricStrategy {

    private final Map<Node, Double> betweennessScores = new HashMap<>();

    @Override
    public void preprocess(Graph graph) {
        return;
    }

    @Override
    public double calculateRank(Node node, Graph graph) {
        if (betweennessScores.isEmpty()) {
            computeBetweenness(graph);
        }
        return betweennessScores.getOrDefault(node, 0.0);
    }

    private void computeBetweenness(Graph graph) {
        for (Node v : graph.getNodes()) {
            betweennessScores.put(v, 0.0);
        }

        for (Node s : graph.getNodes()) {
            Stack<Node> stack = new Stack<>();
            Map<Node, List<Node>> predecessors = new HashMap<>();
            Map<Node, Integer> shortestPaths = new HashMap<>();
            Map<Node, Integer> distance = new HashMap<>();

            Queue<Node> queue = new LinkedList<>();

            for (Node v : graph.getNodes()) {
                predecessors.put(v, new ArrayList<>());
                shortestPaths.put(v, 0);
                distance.put(v, -1);
            }

            shortestPaths.put(s, 1);
            distance.put(s, 0);
            queue.add(s);

            while (!queue.isEmpty()) {
                Node v = queue.poll();
                stack.push(v);
                for (Edge e : graph.getOutgoingEdges(v)) {
                    Node w = e.getDestination();
                    // Path discovery
                    if (distance.get(w) == -1) {
                        distance.put(w, distance.get(v) + 1);
                        queue.add(w);
                    }
                    // Path counting
                    if (distance.get(w) == distance.get(v) + 1) {
                        shortestPaths.put(w, shortestPaths.get(w) + shortestPaths.get(v));
                        predecessors.get(w).add(v);
                    }
                }
            }

            Map<Node, Double> dependency = new HashMap<>();
            for (Node v : graph.getNodes()) {
                dependency.put(v, 0.0);
            }

            while (!stack.isEmpty()) {
                Node w = stack.pop();
                for (Node v : predecessors.get(w)) {
                    double ratio = ((double) shortestPaths.get(v) / shortestPaths.get(w)) * (1.0 + dependency.get(w));
                    dependency.put(v, dependency.get(v) + ratio);
                }
                if (!w.equals(s)) {
                    betweennessScores.put(w, betweennessScores.get(w) + dependency.get(w));
                }
            }
        }

        // Optional: Normalize scores
        int n = graph.getNodes().size();
        if (n > 2) {
            for (Node v : graph.getNodes()) {
                betweennessScores.put(v, betweennessScores.get(v) / ((n - 1) * (n - 2)));
            }
        }
    }

    @Override
    public void normalizeGraph(RankedGraph<? extends Graph> rankedGraph) {
        // Optional: normalize or rank values here
    }
}