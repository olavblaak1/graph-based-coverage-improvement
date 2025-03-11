package com.kuleuven.MissingTestIdentification.SubGraphExtraction.ExtractionAlgorithms;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

import java.util.function.Predicate;

public class GraphExtractionManager {


    public RankedGraph<CoverageGraph> getFullyCoveredGraph(RankedGraph<CoverageGraph> graph) {
        return getGraphUnderCondition(graph, node -> graph.getGraph().isNodeMarked(node),
                edge -> graph.getGraph().isEdgeMarked(edge));
    }

    public RankedGraph<CoverageGraph> getUncoveredGraph(RankedGraph<CoverageGraph> graph) {
        return getGraphUnderCondition(graph, node -> !graph.getGraph().isNodeMarked(node),
                edge -> !graph.getGraph().isEdgeMarked(edge));
    }

    public RankedGraph<CoverageGraph> getGraphUnderCondition(RankedGraph<CoverageGraph> graph, Predicate<Node> nodePred, Predicate<Edge> edgePred) {
        CoverageGraph originalCoverageGraph = graph.getGraph();
        CoverageGraph coverageGraph = new CoverageGraph();

        originalCoverageGraph.getNodes().stream()
                .filter(
                        nodePred)
                .forEach(
                        node -> {
                            coverageGraph.addNode(node);
                            System.out.println(originalCoverageGraph.getMarkedNodeCount(node));
                            coverageGraph.setMarkCount(node, originalCoverageGraph.getMarkedNodeCount(node));
                        });

        originalCoverageGraph.getEdges().stream()
                .filter(
                        edgePred)
                .forEach(
                        edge -> {
                            coverageGraph.addEdge(edge);
                            coverageGraph.setMarkCount(edge, originalCoverageGraph.getMarkedEdgeCount(edge));
                        });

        RankedGraph<CoverageGraph> rankedGraph = new RankedGraph<>(coverageGraph);
        rankedGraph.getGraph().getNodes()
                .forEach(
                        node -> rankedGraph.setRank(node, graph.getRank(node)));
        return rankedGraph;
    }
}
