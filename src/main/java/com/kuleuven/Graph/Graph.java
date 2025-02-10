package com.kuleuven.Graph;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Node.Node;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private final Map<String, Node> nodes;
    private final List<Edge> edges;

    public Graph(Graph graph) {
        this.nodes = graph.getNodes().stream().collect(Collectors.toMap(Node::getName, n -> n));
        this.edges = new ArrayList<>(graph.getEdges());
    }

    public Graph() {
        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>(); // LinkedList
    }

    public Collection<Node> getNodes() {
        return new HashSet<>(nodes.values());
    }

    public Collection<Edge> getEdges() {
        return edges;
    }

    public Node getNode(String name) {
        if (!nodes.containsKey(name)) {
            throw new IllegalArgumentException("Node does not exist");
        }
        return nodes.get(name);
    }

    public void addNode(Node node) {
        nodes.put(node.getName(), node);
    }

    public void addEdge(Edge edge) {
        if (nodeExists(edge.getSource()) && nodeExists(edge.getDestination())) {
            edges.add(edge);
        }
    }

    public void removeNode(String name) {
        if (nodeExists(name)) {
            nodes.remove(name);
        }
    }

    public void removeEdge(Edge edge) {
        if (!nodeExists(edge.getSource()) && !nodeExists(edge.getDestination())) {
            edges.remove(edge);
        } else {
            throw new IllegalArgumentException("Tried to remove edge from existing nodes");
        }
    }

    private boolean nodeExists(String node) {
        return nodes.containsKey(node);
    }
}
