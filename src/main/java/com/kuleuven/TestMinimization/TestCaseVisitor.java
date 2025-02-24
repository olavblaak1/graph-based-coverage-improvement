package com.kuleuven.TestMinimization;

import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public abstract class TestCaseVisitor {
    protected double discountFactor;


    public double calculateImportance(MethodCallEdge edge, RankedGraph<CoverageGraph> graph) {


        double importance = 0.0;
        Deque<Edge> stack = new ArrayDeque<>();
        Set<Edge> visitedEdges = new HashSet<>();

        stack.push(edge);

        while (!stack.isEmpty()) {
            Edge currentEdge = stack.pop();

            if (!visitedEdges.add(currentEdge)) {
                continue;
            }
            importance += getImportance(currentEdge, graph) * discountFactor;
            for (Edge outgoingEdge : graph.getGraph().getOutgoingEdges(currentEdge.getDestination())) {
                stack.push(outgoingEdge);
            }
        }
        return importance;
    }

    public double calculateImportance(InheritanceEdge edge, RankedGraph<CoverageGraph> graph) {
        return getImportance(edge, graph);
    }

    public double calculateImportance(FieldEdge edge, RankedGraph<CoverageGraph> graph) {
        return getImportance(edge, graph);
    }


    public double calculateImportance(OwnedByEdge edge, RankedGraph<CoverageGraph> graph) {
        return 0;
    }


    public double calculateImportance(ClassNode node, RankedGraph<CoverageGraph> graph) {
        return getImportance(node, graph);
    }

    public double calculateImportance(MethodNode node, RankedGraph<CoverageGraph> graph) {
        double importance = getImportance(node, graph);
        for (Edge edge : graph.getGraph().getOutgoingEdges(node)) {
            importance += edge.accept(this, graph);
        }
        return importance;
    }

    public double calculateImportance(OverridesEdge overridesEdge, RankedGraph<CoverageGraph> graph) {
        return getImportance(overridesEdge, graph);
    }

    public double calculateImportance(FieldAccessEdge fieldAccessEdge, RankedGraph<CoverageGraph> graph) {
        return getImportance(fieldAccessEdge, graph);
    }

    abstract double getImportance(Edge edge, RankedGraph<CoverageGraph> graph);
    abstract double getImportance(Node node, RankedGraph<CoverageGraph> graph);
}
