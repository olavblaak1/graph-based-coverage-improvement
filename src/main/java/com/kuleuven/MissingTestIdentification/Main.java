package com.kuleuven.MissingTestIdentification;

import com.kuleuven.CoverageAnalysis.AnalysisResult;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.GraphUtils;
import com.kuleuven.Graph.Serializer.SerializeManager;
import com.kuleuven.TestMinimization.MinimizationMethod;
import org.json.JSONObject;

import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Usage: java TestMinimization <graphPath> <testcase_importance>");
            return;
        }
        AnalysisResult coverageOriginal = AnalysisResult.createFromJson(Objects.requireNonNull(GraphUtils.readAnalysisResults("data/joda-time/analysis/coverageAnalysisResults.json")));

        String graphPath = args[0];
        MinimizationMethod minimizationMethod = MinimizationMethod.valueOf(args[1]);


        JSONObject graphJson = GraphUtils.readGraph(graphPath);

        SerializeManager serializeManager = new SerializeManager();
        RankedGraph<CoverageGraph> SUTGraph = (RankedGraph<CoverageGraph>) serializeManager.deserializeRankedGraph(graphJson);

        /*MissingTestIdentifier missingTestIdentifier = new MissingTestIdentifier();


        GraphUtils.writeFile("data/joda-time/minimizationResults/minimizationResults.json", minimizationResults.toJSON().toString(4).getBytes());*/


    }

}
