package com.kuleuven.GraphExtraction.Graph.Edge;

import com.kuleuven.GraphExtraction.Graph.Node;

/**
 * Abstract class representing an edge in the system graph
 */ 
public abstract class Edge {
    private Node source;
    private Node destination;

    public Edge(Node source, Node destination) {
        this.source = source;
        this.destination = destination;
    }

    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public abstract EdgeType getType();
}