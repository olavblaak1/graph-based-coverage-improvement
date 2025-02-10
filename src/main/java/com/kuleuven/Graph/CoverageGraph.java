package com.kuleuven.Graph;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Node.Node;

import java.util.HashSet;
import java.util.Set;

public class CoverageGraph extends Graph {
    private Set<Node> markedNodes;
    private Set<Edge> markedEdges;


    /*
     * Creates a coverage graph with the given graph. Initially, all nodes and edges
     * are marked as not covered.
     */
    public CoverageGraph(Graph graph) {
        super(graph);
        this.markedNodes = new HashSet<>();
        this.markedEdges = new HashSet<>();
    }

    public CoverageGraph() {
        super();
    }

    public void markNode(Node node) {
        markedNodes.add(node);
    }

    public void markEdge(Edge edge) {
        markedEdges.add(edge);
    }

    public boolean isNodeMarked(Node node) {
        return markedNodes.contains(node);
    }

    public boolean isEdgeMarked(Edge edge) {
        return markedEdges.contains(edge);
    }
}
