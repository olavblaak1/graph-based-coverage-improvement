package com.kuleuven.TestMinimization;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.kuleuven.CoverageAnalysis.AnalysisMethod;
import com.kuleuven.CoverageAnalysis.AnalysisResult;
import com.kuleuven.CoverageAnalysis.CoverageAnalyzer;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.GraphUtils;
import com.kuleuven.Graph.Serializer.SerializeManager;
import com.kuleuven.ParseManager;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/*
    * This class is responsible for minimizing the test suite, it works as follows:
    * 1. It ranks test cases based on the coverage they provide, including the importance of the nodes they cover.
    *       The importance of a node is determined by the number of test cases that cover it and its rank in the graph.
    *       The importance of an edge is determined by the number of test cases that cover it and the importance of the nodes it connects.
    *
    *  The formula used to calculate the importance of a node is:
    *         importance(node) = exp(-number_of_test_cases_covering_node) * rank(node)
    *         OR
    *         importance(node) = exp(-number_of_test_cases_covering_node) * exp(rank(node)) but this would be no different from a linear rank.
    *   Rationale: If a node is covered by many test cases, removing a test case is less likely to affect the coverage of the system, so it is less important.
    *              If a node has a high rank, it is more important to test, so it is better to keep plenty of test-cases.
    *
    *
    *   The formula used to calculate the importance of an edge is:
    *        importance(edge) = exp(-number_of_test_cases_covering_edge) * ((importance(node1) + importance(node2)) / 2)
    *
    *
    *  The importance of a test case is the sum of the importance of the nodes and edges it covers.
    *
    *
    *
    * 2. It returns a list of the most important test cases.
    *
 */
public class Main {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Usage: java TestMinimization <graphPath> <test_directory_path> <jar_path> <src_dir> <testcase_importance>");
            return;
        }
        AnalysisResult coverageOriginal = AnalysisResult.createFromJson(Objects.requireNonNull(GraphUtils.readAnalysisResults("data/joda-time/analysis/coverageAnalysisResults.json")));

        String graphPath = args[0];
        File testDirectory = new File(args[1]);
        Path jarPath = Paths.get(args[2]);
        File srcDir = new File(args[3]);
        //String outputPath = args[4];
        MinimizationMethod minimizationMethod = MinimizationMethod.valueOf(args[4]);


        JSONObject graphJson = GraphUtils.readGraph(graphPath);

        SerializeManager serializeManager = new SerializeManager();
        RankedGraph<? extends Graph> SUTGraph = serializeManager.deserializeRankedGraph(graphJson);

        ParseManager parseManager = new ParseManager();
        List<Path> jarPaths = parseManager.getClasspathJars(jarPath);
        parseManager.setupParser(jarPaths, srcDir);
        parseManager.parseDirectory(testDirectory);

        TestMinimization testMinimization = new TestMinimization(minimizationMethod, 1, 0);
        Map<MethodDeclaration, Double> rankedTests = testMinimization.minimizeTests((RankedGraph<CoverageGraph>) SUTGraph, parseManager.getTestCases());

        double testMinimizationPercentage = 0.25; // keep x% of the most important test cases
        List<MethodDeclaration> filteredTests = rankedTests.entrySet().stream()
                .sorted(Map.Entry.<MethodDeclaration, Double>comparingByValue().reversed())
                .filter(entry -> entry.getValue() > 0)
                .limit((int) Math.ceil(rankedTests.size() * testMinimizationPercentage))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        long relevantTestCount = rankedTests.entrySet().stream().filter(entry -> entry.getValue() > 0).count();
        double realMinimizationPercentage = (double) filteredTests.size() / relevantTestCount;

        CoverageAnalyzer coverageAnalyzer = new CoverageAnalyzer(AnalysisMethod.FULL);


        JSONObject SUTGraphOriginalJSON = GraphUtils.readGraph("data/joda-time/graph/graph.json");
        Graph SUTGraphOriginal = serializeManager.deserializeGraph(SUTGraphOriginalJSON);
        CoverageGraph coverageGraph = coverageAnalyzer.analyze(filteredTests, SUTGraphOriginal);

        AnalysisResult coverageAfterMinimization = new AnalysisResult(coverageGraph);

        MinimizationResult minimizationResults = new MinimizationResult(coverageOriginal, coverageAfterMinimization, realMinimizationPercentage);

        GraphUtils.writeFile("data/joda-time/minimizationResults/minimizationResults.json", minimizationResults.toJSON().toString(4).getBytes());


    }

}
