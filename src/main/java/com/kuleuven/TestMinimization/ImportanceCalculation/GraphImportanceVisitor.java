package com.kuleuven.TestMinimization.ImportanceCalculation;

import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedSharedPath;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Graph.SharedPath;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;

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
    public Collection<RankedSharedPath> getAllPathsWithImportance(Node startNode, RankedGraph<CoverageGraph> graph) {

        // We can do this if we assume the importance is always positive, which means that a longer path is always a better path

        Map<Node, RankedSharedPath> allPaths = new HashMap<>();
        PriorityQueue<RankedSharedPath> queue = new PriorityQueue<>(Comparator.comparingDouble(RankedSharedPath::getRank).reversed());

        RankedSharedPath path = new RankedSharedPath(startNode);
        queue.add(new RankedSharedPath(path));

        allPaths.put(startNode, path);

        while (!queue.isEmpty()) {
            RankedSharedPath currentPath = queue.poll();
            Optional<Node> optCurrentNode = currentPath.getLastNode();
            if (!optCurrentNode.isPresent()) {
                continue;
            }
            Node currentNode = optCurrentNode.get();

            System.out.println("Checking node: " + currentNode);
            for (Edge edge : graph.getGraph().getOutgoingEdges(currentNode)) {
                Node destNode = edge.getDestination();
                if (!allPaths.containsKey(destNode) || allPaths.get(destNode).getRank() <= currentPath.getRank() +
                                                                                        getImportance(edge, graph) +
                                                                                        getImportance(destNode, graph)) {
                    RankedSharedPath newPath = new RankedSharedPath(currentPath);
                    newPath.addNode(destNode, getImportance(edge, graph) + getImportance(destNode, graph));
                    allPaths.put(destNode, newPath);
                    queue.add(newPath);
                }
            }
        }
        System.out.println("Analyzed paths for node " + startNode);
        System.out.println("Paths: " + allPaths.size());

        return allPaths.values().stream()
                .sorted(Comparator.comparingDouble(RankedSharedPath::getRank).reversed())
                .limit(5) // Keep only top 5 paths
                .collect(Collectors.toList());
    }

    protected abstract double getImportance(Edge edge, RankedGraph<CoverageGraph> graph);

    protected abstract double getImportance(Node node, RankedGraph<CoverageGraph> graph);
}
