package com.kuleuven.MissingTestIdentification;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;

public interface WeightingMethod {

    double getImportance(Edge edge, RankedGraph<CoverageGraph> graph);

    double getImportance(Node node, RankedGraph<CoverageGraph> graph);
}
