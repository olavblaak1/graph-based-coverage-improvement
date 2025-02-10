package com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.RankedNode;

import java.util.List;


public interface MetricStrategy {

    List<RankedNode> calculateMetric(List<Node> nodes, List<Edge> edges);

}
