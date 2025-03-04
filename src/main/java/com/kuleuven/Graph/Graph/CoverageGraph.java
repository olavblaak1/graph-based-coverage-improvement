package com.kuleuven.Graph.Graph;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Node.Node;

import java.util.HashMap;
import java.util.Map;

public class CoverageGraph extends Graph {
    private final Map<Node, Integer> markedNodes;
    private final Map<Edge, Integer> markedEdges;


    /*
     * Creates a coverage graph with the given graph. Initially, all nodes and edges
     * are marked as not covered.
     */
    public CoverageGraph(Graph graph) {
        super(graph);
        markedNodes = new HashMap<>();
        markedEdges = new HashMap<>();
    }

    public CoverageGraph() {
        super();
        this.markedEdges = new HashMap<>();
        this.markedNodes = new HashMap<>();
    }

    public void markNode(Node node) {
        if (!markedNodes.containsKey(node)) {
            markedNodes.put(node, 1);
        }
        else {
            markedNodes.put(node, markedNodes.get(node) + 1);
        }
    }

    public void markEdge(Edge edge) {
        if (!markedEdges.containsKey(edge)) {
            markedEdges.put(edge, 1);
        }
        else {
            markedEdges.put(edge, markedEdges.get(edge) + 1);
        }
    }

    public void setMarkCount(Node node, int count) {
        markedNodes.put(node, count);
    }

    public void setMarkCount(Edge edge, int count) {
        markedEdges.put(edge, count);
    }

    public int getMarkedNodeCount(Node node) {
        return markedNodes.getOrDefault(node, 0);
    }

    public int getMarkedEdgeCount(Edge edge) {
        return markedEdges.getOrDefault(edge, 0);
    }

    public boolean isNodeMarked(Node node) {
        return markedNodes.containsKey(node);
    }

    public boolean isEdgeMarked(Edge edge) {
        return markedEdges.containsKey(edge);
    }

    public double getEdgeTypeCoveragePercentage(EdgeType edgeType) {
        if (getEdgesOfType(edgeType).isEmpty()) {
            return 0;
        }
        return (double) getEdgesOfType(edgeType).stream().filter(this::isEdgeMarked).count() / getEdgesOfType(edgeType).size();
    }


    @Override
    public GraphType getType() {
        return GraphType.COVERAGE;
    }
}
