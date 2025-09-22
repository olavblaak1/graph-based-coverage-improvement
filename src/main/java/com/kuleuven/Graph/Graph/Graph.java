package com.kuleuven.Graph.Graph;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.NodeType;
import com.kuleuven.Graph.Node.isOverride;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private final Map<Node, Map<EdgeType, Collection<Edge>>> outgoingEdges;
    private final Map<Node, Map<EdgeType, Collection<Edge>>> incomingEdges;
    private final Map<String, Node> idToNode;

    public Graph(Graph graph) {
        incomingEdges = new HashMap<>();
        outgoingEdges = new HashMap<>();
        idToNode = new HashMap<>();
        for (Node node : graph.getNodes()) {
            addNode(node);
        }
        for (Edge edge : graph.getEdges()) {
            addEdge(edge);
        }
        for (Node node : graph.getNodes()) {
            idToNode.put(node.getId(), node);
        }
    }

    public Integer getSize() {
        return incomingEdges.size();
    }

    public Graph getReachableSubGraph(Node start, Collection<NodeType> nodeTypes, Collection<EdgeType> edgeTypes) {
        Graph reachableSubgraph = new Graph();
        Set<Node> visited = new HashSet<>();
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            Node current = stack.pop();

            if (!visited.add(current)) {
                continue;
            }

            if (nodeTypes.contains(current.getType())) {
                reachableSubgraph.addNode(current);
            }
            for (EdgeType edgeType : edgeTypes) {
                for (Edge edge : getOutgoingEdgesOfType(current, edgeType)) {
                    Node target = edge.getDestination();

                    if (nodeTypes.contains(target.getType())) {
                        reachableSubgraph.addNode(target);
                        reachableSubgraph.addEdge(edge);
                        stack.push(target);
                    }
                }
            }
        }

        return reachableSubgraph;
    }

    public static Optional<String> getMethodID(ResolvedMethodDeclaration node) {
        String name = node.getQualifiedName();
        String signature = node.getSignature();

        return Optional.of(new MethodNode(name, signature, isOverride.UNKNOWN).getId());
    }

    public Graph() {
        outgoingEdges = new HashMap<>();
        incomingEdges = new HashMap<>();
        idToNode = new HashMap<>();
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
        idToNode.put(node.getId(), node);
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
            idToNode.remove(node.getId());
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
        return Optional.ofNullable(idToNode.get(id));
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
