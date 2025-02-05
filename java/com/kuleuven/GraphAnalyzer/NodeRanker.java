package com.kuleuven.GraphAnalyzer;

import java.util.List;

import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.RankedNode;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.FanInFanOutMetric;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.Metric;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.MetricStrategy;

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
