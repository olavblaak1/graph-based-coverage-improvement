package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.GraphUtils;
import com.kuleuven.Graph.Serializer.Graph.BasicGraphSerializer;
import com.kuleuven.Graph.Serializer.Graph.CoverageGraphSerializer;
import com.kuleuven.Graph.Serializer.Graph.GraphSerializer;
import com.kuleuven.ParseManager;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MissingTestFinder {

    public static void main(String[] args) {
        if (args.length != 2 && args.length != 3) {
            System.err.println("Usage: java MissingTestFinder <systemName> <analysisStrategy> optional:<testMethodListPath>");
            return;
        }

        String systemName = args[0];

        String graphPath = "data/" + systemName + "/graph/graph.json";
        File testDirectory = new File("systems/" + systemName + "/src/test/java");
        Path classPaths = Paths.get("systems/" + systemName + "/target/classpath.txt");
        File srcDir = new File("systems/" + systemName + "/src/main/java");
        AnalysisMethod analysisMethod = AnalysisMethod.valueOf(args[1]);
        String outputPath = "data/" + systemName + "/analysis";
        Path jarPath = Paths.get("systems/" + systemName + "/target/targetjars.txt");


        JSONObject graphJson = GraphUtils.readGraph(graphPath);

        GraphSerializer<Graph> graphSerializer = new BasicGraphSerializer();
        Graph SUTGraph = graphSerializer.deserializeGraph(graphJson);

        ParseManager parseManager = new ParseManager();
        List<Path> dependencyJarPaths = parseManager.getClasspathJars(classPaths);
        System.out.println(("dependencyJarPaths count: " + dependencyJarPaths.size()));
        List<Path> compiledJarPaths = parseManager.getClasspathJars(jarPath);

        List<Path> jarPaths = new java.util.ArrayList<>(compiledJarPaths.size() + dependencyJarPaths.size());
        jarPaths.addAll(compiledJarPaths);
        jarPaths.addAll(dependencyJarPaths);

        parseManager.setupParser(jarPaths, List.of(srcDir, testDirectory));
        parseManager.parseDirectory(testDirectory);

        List<MethodDeclaration> testMethods;
        if (args.length == 3) {
            Path testMethodListPath = Paths.get(args[2]);
            try {
                testMethods = parseManager.getFilteredTestCases(testMethodListPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            testMethods = parseManager.getTestCases();
        }

        CoverageAnalyzer coverageAnalyzer = new CoverageAnalyzer(analysisMethod);

        CoverageGraph coverageGraph = coverageAnalyzer.analyze(testMethods, SUTGraph);


        GraphSerializer<CoverageGraph> coverageGraphSerializer = new CoverageGraphSerializer();
        JSONObject coverageGraphJson = coverageGraphSerializer.serializeGraph(coverageGraph);
        AnalysisResult analysisResult = new AnalysisResult(coverageGraph);
        JSONObject analysisResults = analysisResult.toJson();

        if (args.length == 3) {
            GraphUtils.writeFile(outputPath + "/coverageAfterMinimizationGraph.json", coverageGraphJson.toString(4).getBytes());
            GraphUtils.writeFile(outputPath + "/coverageAnalysisResults.json", analysisResults.toString(4).getBytes());
        } else {
            GraphUtils.writeFile(outputPath + "/coverageGraph.json", coverageGraphJson.toString(4).getBytes());
            GraphUtils.writeFile(outputPath + "/coverageAnalysisResults.json", analysisResults.toString(4).getBytes());
        }
    }
}
