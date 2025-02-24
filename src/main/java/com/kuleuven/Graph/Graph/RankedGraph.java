package com.kuleuven.Graph.Graph;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Node.Node;

import java.util.*;

public class RankedGraph<T extends Graph> {

    private T graph;
    private Map<Node, Double> nodeRanks;

    public RankedGraph(T graph) {
        this.graph = graph;
        this.nodeRanks = new HashMap<>();
    }

    public RankedGraph(RankedGraph<T> rankedGraph) {
        this.graph = rankedGraph.getGraph();
        this.nodeRanks = new HashMap<>(rankedGraph.getRanks());
    }

    private Map<Node, Double> getRanks() {
        return nodeRanks;
    }

    public double getRank(Node node) {
        if (!getRanks().containsKey(node)) {
            throw new IllegalArgumentException("Node not in graph");
        }
        return getRanks().get(node);
    }

    public void setRank(Node node, double rank) {
        getRanks().put(node, rank);
    }

    public GraphType graphType() {
        return graph.getType();
    }

    public T getGraph() {
        return graph;
    }

    public Collection<Node> getNodes() {
        return graph.getNodes();
    }

    public Collection<Edge> getEdges() {
        return graph.getEdges();
    }


}
