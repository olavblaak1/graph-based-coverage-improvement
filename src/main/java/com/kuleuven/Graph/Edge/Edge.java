package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.CoverageAnalysis.EdgeAnalysis.CoverageVisitor;
import com.kuleuven.CoverageAnalysis.MarkVisitor.MarkVisitor;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Node.Node;

/**
 * Abstract class representing an edge in the system graph
 */
public abstract class Edge {
    private final Node source;
    private final Node destination;

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

    public abstract boolean accept(CoverageVisitor<ResolvedMethodDeclaration> visitor,  ResolvedMethodDeclaration methodDeclaration);
    public abstract boolean accept(CoverageVisitor<ResolvedFieldDeclaration> visitor,  ResolvedFieldDeclaration fieldDeclaration);
    public abstract void accept(MarkVisitor visitor, CoverageGraph graph);

    @Override
    public String toString() {
        return source + " -> " + destination;
    }
}