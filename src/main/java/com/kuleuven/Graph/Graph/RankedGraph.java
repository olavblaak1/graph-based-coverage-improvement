package com.kuleuven.Graph.Graph;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Node.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RankedGraph<T extends Graph> {

    private final T graph;
    private final Map<Node, Double> nodeRanks;
    private Double maxRank;

    public RankedGraph(T graph) {
        this.graph = graph;
        this.nodeRanks = new HashMap<>();
        this.maxRank = 0.0;
    }

    public boolean hasNode(Node node) {
        return nodeRanks.containsKey(node);
    }

    public RankedGraph(RankedGraph<T> rankedGraph) {
        this.graph = rankedGraph.getGraph();
        this.nodeRanks = new HashMap<>(rankedGraph.getRanks());
        this.maxRank = rankedGraph.getMaxRank();
    }

    public Double getMaxRank() {
        return maxRank;
    }

    private void setMaxRank(Double maxRank) {
        this.maxRank = maxRank;
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
        if (rank > maxRank) {
            setMaxRank(rank);
        }
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
