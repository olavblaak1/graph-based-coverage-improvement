package com.kuleuven.Graph;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Node.Node;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private final Map<Node, Set<Edge>> outgoingEdges;
    private final Map<Node, Set<Edge>> incomingEdges;

    public Graph(Graph graph) {
        incomingEdges = new HashMap<>();
        outgoingEdges = new HashMap<>();
        for (Node node : graph.getNodes()) {
            addNode(node);
        }
        for (Edge edge : graph.getEdges()) {
            addEdge(edge);
        }
    }

    public Graph() {
        outgoingEdges = new HashMap<>();
        incomingEdges = new HashMap<>();
    }

    public Collection<Node> getNodes() {
        return new HashSet<>(incomingEdges.keySet());
    }

    public Collection<Edge> getEdges() {
        return incomingEdges.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public void addNode(Node node) {
        if (!incomingEdges.containsKey(node) && !outgoingEdges.containsKey(node)) {
            incomingEdges.put(node, new HashSet<>());
            outgoingEdges.put(node, new HashSet<>());
        } else if (!incomingEdges.containsKey(node) ^ !outgoingEdges.containsKey(node)) {
            throw new IllegalStateException("Graph is inconsistent");
        }
    }


    public void addEdge(Edge edge) {
        if (nodeExists(edge.getSource()) && nodeExists(edge.getDestination())) {
            outgoingEdges.get(edge.getSource()).add(edge);
            incomingEdges.get(edge.getDestination()).add(edge);
        }
    }

    public void removeNode(Node node) {
        if (nodeExists(node)) {
            incomingEdges.remove(node);
            outgoingEdges.remove(node);
        }
    }

    public void removeEdge(Edge edge) {
        Node source = edge.getSource();
        Node destination = edge.getDestination();
        if (nodeExists(source) && nodeExists(destination)) {
            outgoingEdges.get(source).remove(edge);
            incomingEdges.get(destination).remove(edge);
        }
    }

    private boolean nodeExists(Node node) {
        return incomingEdges.containsKey(node) || outgoingEdges.containsKey(node);
    }

    public Optional<Node> getNode(String id) {
        return incomingEdges.keySet().stream().filter(node -> node.getId().equals(id)).findFirst();
    }

    public Set<Edge> getOutgoingEdges(Node node) {
        return outgoingEdges.get(node);
    }

    public Set<Edge> getIncomingEdges(Node node) {
        return incomingEdges.get(node);
    }
}
