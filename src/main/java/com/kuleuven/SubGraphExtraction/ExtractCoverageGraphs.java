package com.kuleuven.SubGraphExtraction;

import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.GraphUtils;
import com.kuleuven.Graph.Serializer.SerializeManager;
import com.kuleuven.SubGraphExtraction.ExtractionAlgorithms.GraphExtractionManager;
import org.json.JSONObject;

public class ExtractCoverageGraphs {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ExtractCoverageGraphs <systemName>");
            return;
        }
        String systemName = args[0];

        String originalGraphPath = "data/" + systemName + "/analysis/rankedGraph.json";


        JSONObject graphJson = GraphUtils.readGraph(originalGraphPath);
        SerializeManager serializeManager = new SerializeManager();

        RankedGraph<CoverageGraph> rankedGraph;
        try {
            rankedGraph = (RankedGraph<CoverageGraph>) serializeManager.deserializeRankedGraph(graphJson);
        }
        catch (ClassCastException e) {
            throw new RuntimeException("Graph is not a ranked coverage graph");
        }

        GraphExtractionManager graphExtractionManager = new GraphExtractionManager();
        RankedGraph<CoverageGraph> uncoveredGraph = graphExtractionManager.getUncoveredGraph(rankedGraph);
        RankedGraph<CoverageGraph> fullyCoveredGraph = graphExtractionManager.getFullyCoveredGraph(rankedGraph);

        GraphUtils.writeFile("data/" + systemName + "/analysis/fullyCoveredGraph.json", serializeManager.serializeRankedGraph(fullyCoveredGraph).toString(4).getBytes());
        GraphUtils.writeFile("data/" + systemName + "/analysis/uncoveredGraph.json", serializeManager.serializeRankedGraph(uncoveredGraph).toString(4).getBytes());


    }
}
