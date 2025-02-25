package com.kuleuven.Graph;

import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Graph.CoverageGraph;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GraphUtils {

    public static void writeFile(String outputFilePath, byte[] content) {
        try {
            Path path = Paths.get(outputFilePath);
            Path parentDir = path.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            Files.write(path, content);
            System.out.println("File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads the graph json file and returns the graph object
    public static JSONObject readGraph(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject json = new JSONObject(content);
            json.put("nodes", json.getJSONArray("nodes"));
            json.put("edges", json.getJSONArray("edges"));
            return json;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject readAnalysisResults(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONObject(content);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject processAnalysisResults(CoverageGraph coverageGraph) {

        int totalEdges = coverageGraph.getEdges().size();
        int totalNodes = coverageGraph.getNodes().size();

        int totalCoveredEdges = (int) coverageGraph.getEdges().stream().filter(
                coverageGraph::isEdgeMarked).count();
        int totalCoveredNodes = (int) coverageGraph.getNodes().stream().filter(
                coverageGraph::isNodeMarked).count();


        JSONObject analysisResults = new JSONObject();

        analysisResults.put("nodesCoveredPercentage", ((double) totalCoveredNodes / totalNodes));

        analysisResults.put("edgesCoveredPercentage", ((double) totalCoveredEdges / totalEdges));

        analysisResults.put("overridesEdgeCoveredPercentage", coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.OVERRIDES));

        analysisResults.put("fieldAccessEdgesCoveredPercentage", coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.FIELD_ACCESS));

        analysisResults.put("methodCallEdgesCoveredPercentage", coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.METHOD_CALL));

        analysisResults.put("ownedByEdgesCoveredPercentage", coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.OWNED_BY));

        return analysisResults;
    }
}

