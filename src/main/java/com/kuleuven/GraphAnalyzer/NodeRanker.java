package com.kuleuven.GraphAnalyzer;

import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.*;

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
            case HITS:
                metricStrategy = new HITSMetric();
                break;
            case BETWEENNESS:
                metricStrategy = new BetweennessMetric();
                break;
            case PAGERANK:
                metricStrategy = new PageRankMetric();
                break;
            default:
                break;
        }
    }

    public RankedGraph<? extends Graph> rankNodes(Graph graph) {
        return metricStrategy.calculateMetric(graph);
    }
}
