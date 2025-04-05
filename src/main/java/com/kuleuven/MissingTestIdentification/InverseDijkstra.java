package com.kuleuven.MissingTestIdentification;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class InverseDijkstra {


    public static boolean isDAG(RankedGraph<CoverageGraph> graph) {
        Map<Node, Integer> inDegree = new HashMap<>(graph.getNodes().size());  // Track in-degree for each node
        Queue<Node> queue = new ArrayDeque<>();

        // Compute in-degree of each node
        for (Node node : graph.getNodes()) {
            inDegree.put(node, graph.getInDegree(node));
        }

        // Add all nodes with zero in-degree to the queue
        graph.getNodes().forEach(node -> {
            if (inDegree.get(node) == 0) {
                queue.add(node);
            }
        });

        int visitedCount = 0;
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            visitedCount++;

            // Reduce in-degree for neighbors
            for (Edge outgoingEdge : graph.getGraph().getOutgoingEdges(node)) {
                Node neighbor = outgoingEdge.getDestination();
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // If all nodes are visited, it's a DAG
        return visitedCount == graph.getNodes().size();
    }
}
