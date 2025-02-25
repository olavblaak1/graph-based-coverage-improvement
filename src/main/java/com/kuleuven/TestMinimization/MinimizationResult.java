package com.kuleuven.TestMinimization;

import com.kuleuven.CoverageAnalysis.AnalysisResult;
import org.json.JSONObject;

public class MinimizationResult {

    private final double nodeCoverageDecrease;
    private final double edgeCoverageDecrease;
    private final double overridesEdgeCoverageDecrease;
    private final double methodCallEdgeCoverageDecrease;
    private final double ownedByEdgeCoverageDecrease;
    private final double fieldAccessEdgeCoverageDecrease;

    private final double testMinimizationPercentage;

    public MinimizationResult(AnalysisResult originalResult, AnalysisResult minimizationResult, double testMinimizationPercentage) {
        this.nodeCoverageDecrease = (originalResult.getNodesCoveredPercentage() - minimizationResult.getNodesCoveredPercentage()) / originalResult.getNodesCoveredPercentage();
        this.edgeCoverageDecrease = (originalResult.getEdgesCoveredPercentage() - minimizationResult.getEdgesCoveredPercentage()) / originalResult.getEdgesCoveredPercentage();
        this.overridesEdgeCoverageDecrease = (originalResult.getOverridesEdgeCoveredPercentage() - minimizationResult.getOverridesEdgeCoveredPercentage()) / originalResult.getOverridesEdgeCoveredPercentage();
        this.methodCallEdgeCoverageDecrease = (originalResult.getMethodCallEdgeCoveredPercentage() - minimizationResult.getMethodCallEdgeCoveredPercentage()) / originalResult.getMethodCallEdgeCoveredPercentage();
        this.ownedByEdgeCoverageDecrease = (originalResult.getOwnedByEdgeCoveredPercentage() - minimizationResult.getOwnedByEdgeCoveredPercentage()) / originalResult.getOwnedByEdgeCoveredPercentage();
        this.fieldAccessEdgeCoverageDecrease = (originalResult.getFieldAccessEdgeCoveredPercentage() - minimizationResult.getFieldAccessEdgeCoveredPercentage()) / originalResult.getFieldAccessEdgeCoveredPercentage();

        this.testMinimizationPercentage = (1- testMinimizationPercentage);
    }

    public double getNodeCoverageRatio() {
        return testMinimizationPercentage / nodeCoverageDecrease;
    }

    public double getEdgeCoverageRatio() {
        return testMinimizationPercentage / edgeCoverageDecrease;
    }

    public double getOverridesEdgeCoverageRatio() {
        return testMinimizationPercentage / overridesEdgeCoverageDecrease;
    }

    public double getMethodCallEdgeCoverageRatio() {
        return testMinimizationPercentage / methodCallEdgeCoverageDecrease;
    }

    public double getOwnedByEdgeCoverageRatio() {
        return testMinimizationPercentage / ownedByEdgeCoverageDecrease;
    }

    public double getFieldAccessEdgeCoverageRatio() {
        return testMinimizationPercentage / fieldAccessEdgeCoverageDecrease;
    }


    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        JSONObject decrease = new JSONObject();
        decrease.put("nodeCoverageDecrease", nodeCoverageDecrease);
        decrease.put("edgeCoverageDecrease", edgeCoverageDecrease);
        decrease.put("overridesEdgeCoverageDecrease", overridesEdgeCoverageDecrease);
        decrease.put("methodCallEdgeCoverageDecrease", methodCallEdgeCoverageDecrease);
        decrease.put("ownedByEdgeCoverageDecrease", ownedByEdgeCoverageDecrease);
        decrease.put("fieldAccessEdgeCoverageDecrease", fieldAccessEdgeCoverageDecrease);
        decrease.put("testMinimizationPercentage", testMinimizationPercentage);

        json.put("decreasePercentages", decrease);

        JSONObject coverageRatios = new JSONObject();
        coverageRatios.put("nodeCoverageRatio", getNodeCoverageRatio());
        coverageRatios.put("edgeCoverageRatio", getEdgeCoverageRatio());
        coverageRatios.put("overridesEdgeCoverageRatio", getOverridesEdgeCoverageRatio());
        coverageRatios.put("methodCallEdgeCoverageRatio", getMethodCallEdgeCoverageRatio());
        coverageRatios.put("ownedByEdgeCoverageRatio", getOwnedByEdgeCoverageRatio());
        coverageRatios.put("fieldAccessEdgeCoverageRatio", getFieldAccessEdgeCoverageRatio());

        json.put("coverageRatios", coverageRatios);
        return json;
    }
}
