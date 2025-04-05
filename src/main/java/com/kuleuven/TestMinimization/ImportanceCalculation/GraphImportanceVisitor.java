package com.kuleuven.TestMinimization.ImportanceCalculation;

import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedSharedPath;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Graph.SharedPath;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.MissingTestIdentification.InverseDijkstra;

import java.util.*;
import java.util.stream.Collectors;

public abstract class GraphImportanceVisitor {
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

    // Gets all paths from a node using Dijkstra's algorithm
    // Try adapted dijkstras: Assumes that the rank of a node is > 0 and that it is the reciprocal of the testing importance
    public Collection<RankedSharedPath> getAllPathsWithImportance(Node startNode, RankedGraph<CoverageGraph> graph) {

        Map<Node, RankedSharedPath> allPaths = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(node ->
                allPaths.getOrDefault(node, new RankedSharedPath(node, Double.POSITIVE_INFINITY)).getDistance()));

        RankedSharedPath initialPath = new RankedSharedPath(startNode, 0.0);
        allPaths.put(startNode, initialPath);
        queue.add(startNode);



        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            System.out.println("Current node: " + currentNode.getSimpleName());
            System.out.println("Outgoing edges: " + new HashSet<>(graph.getGraph().getOutgoingEdges(currentNode)));
            RankedSharedPath currentPath = allPaths.get(currentNode);
            if (visited.contains(currentNode)) {
                continue;
            }
            visited.add(currentNode);

            for (Edge edge : graph.getGraph().getOutgoingEdges(currentNode)) {
                Node destNode = edge.getDestination();

                if (visited.contains(destNode)) {
                    continue;
                }

                System.out.println("Checking node: " + destNode.getSimpleName());
                RankedSharedPath newPath = new RankedSharedPath(currentPath);
                newPath.addNode(destNode, getImportance(edge, graph));

                if (!allPaths.containsKey(destNode) || newPath.getDistance() < allPaths.get(destNode).getDistance()) {
                    System.out.println("New path: " + currentNode.getSimpleName() + " -> " + destNode.getSimpleName());
                    allPaths.put(destNode, newPath);
                    queue.add(destNode);
                }
            }
        }
        // Perhaps not necessary, as the length of paths should be considered when looking for paths
        // that are useful to test.
        return allPaths.values().stream().filter(path -> Double.isFinite(path.getDistance()) && path.getSize() > 1).collect(Collectors.toList());
    }


    protected abstract double getImportance(Edge edge, RankedGraph<CoverageGraph> graph);

    protected abstract double getImportance(Node node, RankedGraph<CoverageGraph> graph);

    public Collection<RankedSharedPath> getAllPathsWithImportanceBruteForce(Node node, RankedGraph<CoverageGraph> graph) {
        throw new UnsupportedOperationException("Brute force path calculation not implemented");
    }
}
