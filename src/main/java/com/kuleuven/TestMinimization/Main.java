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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
        if (args.length != 2) {
            System.err.println("Usage: java TestMinimization <systemName> <minimizationMetric>");
            return;
        }
        String systemName = args[0];

        String originalGraphPath = "data/" + systemName + "/analysis/rankedGraph.json";
        String originalAnalysisResults = "data/" + systemName + "/analysis/coverageAnalysisResults.json";
        File testDirectory = new File("systems/" + systemName + "/src/test/java");
        Path classPaths = Paths.get("systems/" + systemName + "/target/classpath.txt");
        File srcDir = new File("systems/" + systemName + "/src/main/java");
        String outputPath = "data/" + systemName + "/minimization/minimizationResults.json";
        MinimizationMethod minimizationMethod = MinimizationMethod.valueOf(args[1]);
        Path jarPath = Paths.get("systems/" + systemName + "/target/targetjars.txt");


        JSONObject graphJson = GraphUtils.readGraph(originalGraphPath);

        SerializeManager serializeManager = new SerializeManager();
        RankedGraph<? extends Graph> SUTGraph = serializeManager.deserializeRankedGraph(graphJson);

        ParseManager parseManager = new ParseManager();
        List<Path> dependencyJarPaths = parseManager.getClasspathJars(classPaths);
        List<Path> compiledJarPaths = parseManager.getClasspathJars(jarPath);
        List<Path> jarPaths = Stream.of(dependencyJarPaths, compiledJarPaths).flatMap(List::stream).collect(Collectors.toList());

        parseManager.setupParser(jarPaths, List.of(srcDir, testDirectory));
        parseManager.parseDirectory(testDirectory);

        TestMinimization testMinimization = new TestMinimization(minimizationMethod, 1, 0);
        Map<MethodDeclaration, Double> rankedTests = testMinimization.minimizeTests((RankedGraph<CoverageGraph>) SUTGraph, parseManager.getTestCases());

        double testMinimizationPercentage = 0.5; // keep x% of the most important test cases
        List<MethodDeclaration> filteredTests = rankedTests.entrySet().stream()
                .sorted(Map.Entry.<MethodDeclaration, Double>comparingByValue().reversed())
                .limit((int) Math.ceil(rankedTests.size() * testMinimizationPercentage))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        double realMinimizationPercentage = (double) filteredTests.size() / rankedTests.size();

        CoverageAnalyzer coverageAnalyzer = new CoverageAnalyzer(AnalysisMethod.FULL);


        JSONObject SUTGraphOriginalJSON = GraphUtils.readGraph(originalGraphPath);
        Graph SUTGraphOriginal = serializeManager.deserializeGraph(SUTGraphOriginalJSON);
        CoverageGraph coverageGraph = coverageAnalyzer.analyze(filteredTests, SUTGraphOriginal);

        AnalysisResult coverageAfterMinimization = new AnalysisResult(coverageGraph);

        AnalysisResult coverageOriginal = AnalysisResult.createFromJson(Objects.requireNonNull(GraphUtils.readAnalysisResults(originalAnalysisResults)));
        MinimizationResult minimizationResults = new MinimizationResult(coverageOriginal, coverageAfterMinimization, realMinimizationPercentage);

        GraphUtils.writeFile(outputPath, minimizationResults.toJSON().toString(4).getBytes());

        JSONObject tests = new JSONObject();

        JSONArray minimizedTests = new JSONArray();
        filteredTests.forEach(test -> {
            JSONObject testObject = new JSONObject();
            testObject.put("name", test.resolve().getQualifiedName());
            testObject.put("rank", rankedTests.get(test));
            minimizedTests.put(testObject);
        });

        tests.put("minimizedTests", minimizedTests);
        GraphUtils.writeFile("data/" + systemName + "/minimization/minimizedTests.json", tests.toString(4).getBytes());
    }

}
