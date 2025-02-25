package com.kuleuven.CoverageAnalysis;

import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Edge.EdgeType;
import org.json.JSONObject;

public class AnalysisResult {
    double nodesCoveredPercentage;
    double edgesCoveredPercentage;
    double overridesEdgeCoveredPercentage;
    double methodCallEdgeCoveredPercentage;
    double ownedByEdgeCoveredPercentage;
    double fieldAccessEdgeCoveredPercentage;

    public AnalysisResult() {

    }

    public AnalysisResult(CoverageGraph coverageGraph) {
        int totalEdges = coverageGraph.getEdges().size();
        int totalNodes = coverageGraph.getNodes().size();

        int totalCoveredEdges = (int) coverageGraph.getEdges().stream().filter(
                coverageGraph::isEdgeMarked).count();
        int totalCoveredNodes = (int) coverageGraph.getNodes().stream().filter(
                coverageGraph::isNodeMarked).count();

        this.nodesCoveredPercentage = ((double) totalCoveredNodes / totalNodes);
        this.edgesCoveredPercentage = ((double) totalCoveredEdges / totalEdges);
        this.overridesEdgeCoveredPercentage = coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.OVERRIDES);
        this.methodCallEdgeCoveredPercentage = coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.METHOD_CALL);
        this.ownedByEdgeCoveredPercentage = coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.OWNED_BY);
        this.fieldAccessEdgeCoveredPercentage = coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.FIELD_ACCESS);
    }

    public double getNodesCoveredPercentage() {
        return nodesCoveredPercentage;
    }

    public double getEdgesCoveredPercentage() {
        return edgesCoveredPercentage;
    }

    public double getOverridesEdgeCoveredPercentage() {
        return overridesEdgeCoveredPercentage;
    }

    public double getMethodCallEdgeCoveredPercentage() {
        return methodCallEdgeCoveredPercentage;
    }

    public double getOwnedByEdgeCoveredPercentage() {
        return ownedByEdgeCoveredPercentage;
    }

    public double getFieldAccessEdgeCoveredPercentage() {
        return fieldAccessEdgeCoveredPercentage;
    }

    public JSONObject toJson() {
        JSONObject analysisResults = new JSONObject();
        analysisResults.put("nodesCoveredPercentage", nodesCoveredPercentage);
        analysisResults.put("edgesCoveredPercentage", edgesCoveredPercentage);
        analysisResults.put("overridesEdgeCoveredPercentage", overridesEdgeCoveredPercentage);
        analysisResults.put("methodCallEdgeCoveredPercentage", methodCallEdgeCoveredPercentage);
        analysisResults.put("ownedByEdgeCoveredPercentage", ownedByEdgeCoveredPercentage);
        analysisResults.put("fieldAccessEdgeCoveredPercentage", fieldAccessEdgeCoveredPercentage);
        return analysisResults;
    }

    public static AnalysisResult createFromJson(JSONObject analysisResultJson) {
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.nodesCoveredPercentage = analysisResultJson.getDouble("nodesCoveredPercentage");
        analysisResult.edgesCoveredPercentage = analysisResultJson.getDouble("edgesCoveredPercentage");
        analysisResult.overridesEdgeCoveredPercentage = analysisResultJson.getDouble("overridesEdgeCoveredPercentage");
        analysisResult.methodCallEdgeCoveredPercentage = analysisResultJson.getDouble("methodCallEdgeCoveredPercentage");
        analysisResult.ownedByEdgeCoveredPercentage = analysisResultJson.getDouble("ownedByEdgeCoveredPercentage");
        analysisResult.fieldAccessEdgeCoveredPercentage = analysisResultJson.getDouble("fieldAccessEdgeCoveredPercentage");
        return analysisResult;
    }

    public String toString() {
        return toJson().toString(4);
    }


}
