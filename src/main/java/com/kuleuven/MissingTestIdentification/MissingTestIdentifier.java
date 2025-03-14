package com.kuleuven.MissingTestIdentification;

import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Graph.RankedSharedPath;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.TestMinimization.ImportanceCalculation.GraphImportanceVisitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MissingTestIdentifier {

    GraphImportanceVisitor graphImportanceVisitor = new MissingTestImportanceVisitor();

    public MissingTestList findMissingTests(RankedGraph<CoverageGraph> graph) {
        Map<Node, Double> missingTests = new HashMap<>();
        graph.getGraph().getNodes().forEach(node ->
                        missingTests.put(node, analyzeMethod(node, graph)));
        return new MissingTestList(missingTests);
    }

    public MissingPathList findMissingPaths(RankedGraph<CoverageGraph> graph) {
        Map<Node, Collection<RankedSharedPath>> missingPaths = new HashMap<>();

        // Invert the ranks, so that the most important nodes have the lowest rank, this is
        // necessary for Dijkstra's algorithm to work correctly (to transform the longest path problem to
        // the shortest path problem). This is done by taking the reciprocal of the rank,
        // it does not change the respective rankings of the nodes, but the total path rank may be different.
        // compared to the test coverage ranking
        Double maxRank = graph.getMaxRank();
        graph.mapRanks(rank -> rank / maxRank);
        graph.getGraph().getNodes().forEach(node ->
                        missingPaths.put(node, graphImportanceVisitor.getAllPathsWithImportance(node, graph)));
        graph.mapRanks(rank -> rank / maxRank);
        return new MissingPathList(missingPaths, findMissingTests(graph));
    }

    private Double analyzeMethod(Node node, RankedGraph<CoverageGraph> graph) {
        if (graph.getGraph().isNodeMarked(node)) {
            throw new RuntimeException("Graph should be uncovered, with no marked nodes");
        }
        return node.accept(graphImportanceVisitor, graph);
    }

}
