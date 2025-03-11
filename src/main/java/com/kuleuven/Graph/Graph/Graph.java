package com.kuleuven.Graph.Graph;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Node.Node;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private final Map<Node, Map<EdgeType, Collection<Edge>>> outgoingEdges;
    private final Map<Node, Map<EdgeType, Collection<Edge>>> incomingEdges;

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
        return incomingEdges.values().stream().flatMap(map -> map.values().stream().flatMap(Collection::stream)).collect(Collectors.toList());
    }

    public void addNode(Node node) {
        if (!incomingEdges.containsKey(node) && !outgoingEdges.containsKey(node)) {
            initializeNode(node);
        } else if (!incomingEdges.containsKey(node) ^ !outgoingEdges.containsKey(node)) {
            throw new IllegalStateException("Graph is inconsistent");
        }
    }

    private void initializeNode(Node node) {
        incomingEdges.put(node, new HashMap<>());
        outgoingEdges.put(node, new HashMap<>());
        for (EdgeType type : EdgeType.values()) {
            incomingEdges.get(node).put(type, new HashSet<>());
            outgoingEdges.get(node).put(type, new HashSet<>());
        }
    }


    public void addEdge(Edge edge) {
        if (nodeExists(edge.getSource()) && nodeExists(edge.getDestination())) {
            incomingEdges.get(edge.getDestination()).get(edge.getType()).add(edge);
            outgoingEdges.get(edge.getSource()).get(edge.getType()).add(edge);
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

    public boolean nodeExists(Node node) {
        return incomingEdges.containsKey(node) && outgoingEdges.containsKey(node);
    }

    public Optional<Node> getNode(String id) {
        return incomingEdges.keySet().stream().filter(node -> node.getId().equals(id)).findFirst();
    }

    public Collection<Edge> getOutgoingEdgesOfType(Node node, EdgeType type) {
        return outgoingEdges.get(node).get(type);
    }

    public Collection<Edge> getOutgoingEdges(Node node) {
        return outgoingEdges.get(node).values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public Collection<Edge> getIncomingEdges(Node node) {
        return incomingEdges.get(node).values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public Collection<Edge> getIncomingEdgesOfType(Node node, EdgeType type) {
        return incomingEdges.get(node).get(type);
    }

    public int getFanInPlusFanOut(Node node) {
        return getOutgoingEdges(node).size() + getIncomingEdges(node).size();
    }

    public int getFanIn(Node node) {
        return getIncomingEdges(node).size();
    }

    public int getFanOut(Node node) {
        return getOutgoingEdges(node).size();
    }

    public Set<Edge> getEdgesOfType(EdgeType type) {
        Set<Edge> edges = new HashSet<>();
        for (Node node : incomingEdges.keySet()) {
            edges.addAll(incomingEdges.get(node).get(type));
            edges.addAll(outgoingEdges.get(node).get(type));
        }
        return edges;
    }

    public GraphType getType() {
        return GraphType.BASIC;
    }
}
