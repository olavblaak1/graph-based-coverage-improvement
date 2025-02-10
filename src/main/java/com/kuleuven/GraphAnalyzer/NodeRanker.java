package com.kuleuven.GraphAnalyzer;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.RankedNode;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.FanInFanOutMetric;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.Metric;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.MetricStrategy;

import java.util.List;

public class NodeRanker {

    private MetricStrategy metricStrategy;


    public NodeRanker(Metric metric) {
        switch (metric) {
            case FAN_IN_AND_FAN_OUT:
                metricStrategy = new FanInFanOutMetric();
                break;
            default:
                break;
        }
    }

    public List<RankedNode> rankNodes(List<Node> nodes, List<Edge> edges) {
        return metricStrategy.calculateMetric(nodes, edges);
    }
}
