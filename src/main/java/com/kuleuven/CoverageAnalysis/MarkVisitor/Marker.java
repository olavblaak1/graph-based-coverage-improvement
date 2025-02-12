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
        Deque<Edge> stack = new ArrayDeque<>();
        Set<Edge> visitedEdges = new HashSet<>();

        stack.push(startEdge);

        while (!stack.isEmpty()) {
            Edge currentEdge = stack.pop();

            if (!visitedEdges.add(currentEdge)) {
                continue;
            }

            graph.markEdge(currentEdge);
            graph.getOutgoingEdges(currentEdge.getDestination())
                    .stream()
                    .filter(edge -> edge instanceof MethodCallEdge && !graph.isEdgeMarked(edge))
                    .forEach(stack::push);
        }
    }

    @Override
    public void mark(InheritanceEdge edge, CoverageGraph graph) {
        // Figure this one out (need extra info)
    }

    @Override
    public void mark(FieldEdge edge, CoverageGraph graph) {
        // Figure this one out (need extra info)
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
        graph.getOutgoingEdges(node).forEach(edge -> edge.accept(this, graph));
    }

    @Override
    public void mark(OverridesEdge overridesEdge, CoverageGraph graph) {
        graph.markEdge(overridesEdge);
    }
}
