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
        this.nodeCoverageDecrease = sanitize((originalResult.getNodesCoveredPercentage() - minimizationResult.getNodesCoveredPercentage()), originalResult.getNodesCoveredPercentage());
        this.edgeCoverageDecrease = sanitize((originalResult.getEdgesCoveredPercentage() - minimizationResult.getEdgesCoveredPercentage()), originalResult.getEdgesCoveredPercentage());
        this.overridesEdgeCoverageDecrease = sanitize((originalResult.getOverridesEdgeCoveredPercentage() - minimizationResult.getOverridesEdgeCoveredPercentage()), originalResult.getOverridesEdgeCoveredPercentage());
        this.methodCallEdgeCoverageDecrease = sanitize((originalResult.getMethodCallEdgeCoveredPercentage() - minimizationResult.getMethodCallEdgeCoveredPercentage()), originalResult.getMethodCallEdgeCoveredPercentage());
        this.ownedByEdgeCoverageDecrease = sanitize((originalResult.getOwnedByEdgeCoveredPercentage() - minimizationResult.getOwnedByEdgeCoveredPercentage()), originalResult.getOwnedByEdgeCoveredPercentage());
        this.fieldAccessEdgeCoverageDecrease = sanitize((originalResult.getFieldAccessEdgeCoveredPercentage() - minimizationResult.getFieldAccessEdgeCoveredPercentage()), originalResult.getFieldAccessEdgeCoveredPercentage());

        this.testMinimizationPercentage = (1- sanitize(testMinimizationPercentage, 1));
    }

    public double getNodeCoverageRatio() {
        return sanitize(testMinimizationPercentage, nodeCoverageDecrease);
    }

    public double getEdgeCoverageRatio() {
        return sanitize(testMinimizationPercentage, edgeCoverageDecrease);
    }

    public double getOverridesEdgeCoverageRatio() {
        return sanitize(testMinimizationPercentage, overridesEdgeCoverageDecrease);
    }

    public double getMethodCallEdgeCoverageRatio() {
        return sanitize(testMinimizationPercentage, methodCallEdgeCoverageDecrease);
    }

    public double getOwnedByEdgeCoverageRatio() {
        return sanitize(testMinimizationPercentage, ownedByEdgeCoverageDecrease);
    }

    public double getFieldAccessEdgeCoverageRatio() {
        return sanitize(testMinimizationPercentage, fieldAccessEdgeCoverageDecrease);
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

    private double sanitize(double numerator, double denominator) {

        if (denominator == 0 && numerator == 0) {
            return 1;
        }
        if (denominator == 0) {
            return 0;
        }
        return numerator / denominator;
    }
}
