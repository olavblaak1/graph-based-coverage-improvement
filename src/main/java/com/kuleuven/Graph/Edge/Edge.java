package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

/**
 * Abstract class representing an edge in the system graph
 */
public abstract class Edge {
    private final String source;
    private final String destination;

    public Edge(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
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

    public abstract boolean isCoveredBy(ResolvedMethodDeclaration methodDeclaration);
}