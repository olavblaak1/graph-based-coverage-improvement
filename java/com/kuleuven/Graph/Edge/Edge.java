package com.kuleuven.Graph.Edge;

import com.kuleuven.Graph.Node;

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

    @Override
    public int hashCode() {
        return source.hashCode() + destination.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Edge)) {
            return false;
        }
        Edge edge = (Edge) obj;
        return source.equals(edge.getSource()) && destination.equals(edge.getDestination());
    }
}