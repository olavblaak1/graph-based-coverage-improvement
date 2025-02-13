package com.kuleuven.CoverageAnalysis.MarkVisitor;

import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;

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
        graph.markEdge(edge);
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
        Node destination = overridesEdge.getDestination();

        String classID = graph.getOutgoingEdges(destination).stream().filter(e -> (e.getType().equals(EdgeType.OWNED_BY))).findFirst().get().getDestination().getId();

        // This covers the inheritance edge associated with the method override, in essence this is very conservative
        // For example, if class1 overrides a method of class2, it must inherit from class2
        // The following ensures the inheritance is also marked, although very conservatively
        // As it already marks an entire inheritance edge when only one overridden method is covered
        graph.getOutgoingEdges(destination).stream().filter(edge ->
                edge.getType().equals(EdgeType.OWNED_BY)).forEach(edge -> {
                    edge.accept(this, graph);
                    graph.getIncomingEdges(edge.getDestination()).stream().filter(e ->
                            (e.getType().equals(EdgeType.INHERITANCE))).filter(inheritanceEdge ->
                            inheritanceEdge.getDestination().getId().equals(classID)).forEach(matchingInheritanceEdge ->
                                matchingInheritanceEdge.accept(this, graph));
                });
    }

    @Override
    public void mark(FieldAccessEdge fieldAccessEdge, CoverageGraph graph) {

    }
}
