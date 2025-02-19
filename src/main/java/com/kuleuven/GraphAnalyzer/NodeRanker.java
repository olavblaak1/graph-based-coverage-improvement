package com.kuleuven.GraphAnalyzer;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.RankedNode;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.FanInFanOutMetric;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.Metric;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.MetricStrategy;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.NOOMMetric;

import java.util.List;

public class NodeRanker {

    private MetricStrategy metricStrategy;


    public NodeRanker(Metric metric) {
        switch (metric) {
            case FAN_IN_AND_FAN_OUT:
                metricStrategy = new FanInFanOutMetric();
                break;
            case NOOM:
                metricStrategy = new NOOMMetric();
                break;
            default:
                break;
        }
    }

    public RankedGraph<? extends Graph> rankNodes(Graph graph) {
        return metricStrategy.calculateMetric(graph);
    }
}
