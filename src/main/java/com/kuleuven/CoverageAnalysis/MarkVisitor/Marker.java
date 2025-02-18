package com.kuleuven.CoverageAnalysis.MarkVisitor;

import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class Marker implements MarkVisitor {

    // Mark one edge and all of its called methods recursively
    @Override
    public void mark(MethodCallEdge startEdge, CoverageGraph graph) {
        // TODO: decide what to do here, do we cover all possible method calls that could possibly come from this one?
        // or just one? or none?
        // if none -> We focus on UNIT tests, if > 0 we start to look at integration testing, both might be interesting
        graph.markEdge(startEdge);
        /*
        Deque<Edge> stack = new ArrayDeque<>();
        Set<Edge> visitedEdges = new HashSet<>();

        stack.push(startEdge);

        while (!stack.isEmpty()) {
            Edge currentEdge = stack.pop();

            if (!visitedEdges.add(currentEdge)) {
                continue;
            }

            graph.markEdge(currentEdge);
            graph.markNode(currentEdge.getDestination()); // This allows for integration testing coverage, may not be necessary..
        }*/
    }

    @Override
    public void mark(InheritanceEdge edge, CoverageGraph graph) {
        graph.markEdge(edge);
    }

    @Override
    public void mark(FieldEdge edge, CoverageGraph graph) {
        graph.markEdge(edge);
    }

    @Override
    public void mark(OwnedByEdge edge, CoverageGraph graph) {
        graph.markEdge(edge);
        graph.markNode(edge.getDestination());
    }

    @Override
    public void mark(ClassNode node, CoverageGraph graph) {
        graph.markNode(node);
    }

    @Override
    public void mark(MethodNode node, CoverageGraph graph) {
        graph.markNode(node);
        // If a method is marked, mark all of it's outgoing edges, this assumes all branches are taken!
        graph.getOutgoingEdges(node).forEach(edge -> edge.accept(this, graph));
    }

    @Override
    public void mark(OverridesEdge overridesEdge, CoverageGraph graph) {
        graph.markEdge(overridesEdge);
    }

    @Override
    public void mark(FieldAccessEdge fieldAccessEdge, CoverageGraph graph) {
        graph.markEdge(fieldAccessEdge);
    }
}
