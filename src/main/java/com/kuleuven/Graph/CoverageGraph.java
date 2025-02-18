package com.kuleuven.Graph;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Node.Node;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CoverageGraph extends Graph {
    private Set<Node> markedNodes;
    private Set<Edge> markedEdges;


    /*
     * Creates a coverage graph with the given graph. Initially, all nodes and edges
     * are marked as not covered.
     */
    public CoverageGraph(Graph graph) {
        super(graph);
        markedNodes = new HashSet<>();
        markedEdges = new HashSet<>();
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

    public double getEdgeTypeCoveragePercentage(EdgeType edgeType) {
        return (double) getEdgesOfType(edgeType).stream().filter(this::isEdgeMarked).count() / getEdgesOfType(edgeType).size();
    }


    public long getCoveredEdgesOfNodeCount(Node node) {
        return super.getOutgoingEdges(node).stream().filter(this::isEdgeMarked).count()
                +
                super.getIncomingEdges(node).stream().filter(this::isEdgeMarked).count();
    }

    public long coveredCountOfType(Node node, EdgeType type) {
        return super.getOutgoingEdgesOfType(node, type).stream().filter(this::isEdgeMarked).count()
                +
                super.getIncomingEdgesOfType(node, type).stream().filter(this::isEdgeMarked).count();
    }

    public double coveredPercentage(Node node) {
        if (!markedNodes.contains(node)) {
            return 1.0;
        }
        return (double) getCoveredEdgesOfNodeCount(node) / getFanInPlusFanOut(node);
    }

    public double coveredPercentageOfType(Node node, EdgeType type) {
        if (!markedNodes.contains(node)) {
            return 1.0;
        }
        return (double) coveredCountOfType(node, type)
                        /
                        (getOutgoingEdgesOfType(node, type).size() + getIncomingEdgesOfType(node, type).size());
    }
}
