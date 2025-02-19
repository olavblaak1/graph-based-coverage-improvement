package com.kuleuven.GraphAnalyzer;

import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.GraphUtils;
import com.kuleuven.Graph.Serializer.SerializeManager;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.Metric;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java -jar GraphAnalyzer.jar <path-to-graph-file> <metric> <output-file>");
        }
        String graphPath = args[0];
        Metric metric = Metric.valueOf(args[1]);
        String outputPath = args[2];

        JSONObject graphJson = GraphUtils.readGraph(graphPath);
        SerializeManager serializeManager = new SerializeManager();

        Graph graph = serializeManager.deserializeGraph(graphJson);
        NodeRanker nodeRanker = new NodeRanker(metric);
        RankedGraph<? extends Graph> rankedGraph = nodeRanker.rankNodes(graph);

        GraphUtils.writeFile(outputPath, serializeManager.serializeRankedGraph(rankedGraph).toString(4).getBytes());
    }
}
