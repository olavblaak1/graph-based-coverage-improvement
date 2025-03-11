package com.kuleuven.MissingTestIdentification;

import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.GraphUtils;
import com.kuleuven.Graph.Serializer.SerializeManager;
import com.kuleuven.MissingTestIdentification.SubGraphExtraction.ExtractionAlgorithms.GraphExtractionManager;
import com.kuleuven.ParseManager;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java TestMinimization <systemname>");
            return;
        }



        String systemName = args[0];

        String graphPath = "data/" + systemName + "/analysis/uncoveredGraph.json";
        Path classPaths = Paths.get("systems/" + systemName + "/target/classpath.txt");
        File srcDir = new File("systems/" + systemName + "/src/main/java");
        Path jarPath = Paths.get("systems/" + systemName + "/target/targetjars.txt");


        JSONObject graphJson = GraphUtils.readGraph(graphPath);

        ParseManager parseManager = new ParseManager();
        List<Path> dependencyJarPaths = parseManager.getClasspathJars(classPaths);
        System.out.println(("dependencyJarPaths count: " + dependencyJarPaths.size()));
        List<Path> compiledJarPaths = parseManager.getClasspathJars(jarPath);

        List<Path> jarPaths = new java.util.ArrayList<>(compiledJarPaths.size() + dependencyJarPaths.size());
        jarPaths.addAll(compiledJarPaths);
        jarPaths.addAll(dependencyJarPaths);

        parseManager.setupParser(jarPaths, List.of(srcDir));
        parseManager.parseDirectory(srcDir);

        SerializeManager serializeManager = new SerializeManager();
        RankedGraph<CoverageGraph> rankedGraph;
        try {
            rankedGraph = (RankedGraph<CoverageGraph>) serializeManager.deserializeRankedGraph(graphJson);
        } catch (ClassCastException e) {
            throw new RuntimeException("Graph is not a ranked coverage graph");
        }

        GraphExtractionManager graphExtractionManager = new GraphExtractionManager();
        RankedGraph<CoverageGraph> uncoveredGraph = graphExtractionManager.getUncoveredGraph(rankedGraph);

        MissingTestIdentifier missingTestIdentifier = new MissingTestIdentifier();
        MissingTestList missingTests = missingTestIdentifier.findMissingTests(uncoveredGraph);

        System.out.println("Finding missing test methods...");
        String outputPath = "data/" + systemName + "/missing_tests/missingTestMethods.json";
        GraphUtils.writeFile(outputPath, missingTests.toJSON().toString(4).getBytes());

        System.out.println("Finding missing test paths...");
        outputPath = "data/" + systemName + "/missing_tests/missingTestPaths.json";
        MissingPathList missingPaths = missingTestIdentifier.findMissingPaths(uncoveredGraph);
        GraphUtils.writeFile(outputPath, missingPaths.toJSON().toString(4).getBytes());


    }

}
