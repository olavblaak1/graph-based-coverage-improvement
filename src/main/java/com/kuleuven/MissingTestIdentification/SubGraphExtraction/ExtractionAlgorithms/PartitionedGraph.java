package com.kuleuven.MissingTestIdentification.SubGraphExtraction.ExtractionAlgorithms;

import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;

public class PartitionedGraph {
    RankedGraph<CoverageGraph> uncoveredGraph;
    RankedGraph<CoverageGraph> partiallyCoveredGraph;
    RankedGraph<CoverageGraph> coveredGraph;

    public PartitionedGraph(RankedGraph<CoverageGraph> uncoveredGraph, RankedGraph<CoverageGraph> partiallyCoveredGraph, RankedGraph<CoverageGraph> coveredGraph) {
        this.uncoveredGraph = uncoveredGraph;
        this.partiallyCoveredGraph = partiallyCoveredGraph;
        this.coveredGraph = coveredGraph;
    }

    public RankedGraph<CoverageGraph> getUncoveredGraph() {
        return uncoveredGraph;
    }

    public RankedGraph<CoverageGraph> getPartiallyCoveredGraph() {
        return partiallyCoveredGraph;
    }

    public RankedGraph<CoverageGraph> getCoveredGraph() {
        return coveredGraph;
    }
}
