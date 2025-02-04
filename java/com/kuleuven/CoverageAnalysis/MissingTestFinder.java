package com.kuleuven.CoverageAnalysis;

import org.json.JSONArray;
import org.json.JSONObject;
import com.kuleuven.ParseManager;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.GraphUtils;
import com.kuleuven.Graph.MethodNode;
import com.kuleuven.Graph.NodeType;
import com.kuleuven.Graph.Serializer.SerializeManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MissingTestFinder {

    public static void main(String[] args) {
        if (args.length < 5) {
            System.err.println("Usage: java MissingTestFinder <graphPath> <test_directory_path> <jar_path> <src_dir> <analysisStrategy>");
            return;
        }
        String graphPath = args[0];
        File testDirectory = new File(args[1]);
        Path jarPath = Paths.get(args[2]);
        File srcDir = new File(args[3]);
        AnalysisStrategy analysisStrategy = AnalysisStrategy.valueOf(args[4]);


        SerializeManager serializeManager = new SerializeManager();
        JSONObject graphJson = GraphUtils.readGraph(graphPath);
        Graph SUTGraph = serializeManager.deserializeGraph(graphJson);

        ParseManager parseManager = new ParseManager();
        List<Path> jarPaths = Arrays.asList(jarPath,
                                            Paths.get("target/libs/junit-4.13.2.jar"),
                                            Paths.get("target/libs/junit-jupiter-engine-5.2.0.jar"),
                                            Paths.get("target/libs/junit-platform-runner-1.2.0.jar"),
                                            Paths.get("target/libs/junit-jupiter-api-5.2.0.jar")
        );
        parseManager.setupParser(jarPaths, srcDir);
        parseManager.parseDirectory(testDirectory);


        CoverageAnalyzer coverageAnalyzer = new CoverageAnalyzer(analysisStrategy);
        CoverageGraph coverageGraph = coverageAnalyzer.analyze(parseManager.getCompilationUnits(), SUTGraph, parseManager.getSolver());


        int totalEdges = coverageGraph.getEdges().size();
        int totalNodes = coverageGraph.getNodes().size();

        int totalCoveredEdges = (int) coverageGraph.getEdges().stream().filter(
                coverageGraph::isEdgeMarked).count();
        int totalCoveredNodes = (int) coverageGraph.getNodes().stream().filter(
                coverageGraph::isNodeMarked).count();



        System.out.println("Total number of edges: " + totalEdges);
        System.out.println("Total number of edges covered: " + totalCoveredEdges);
        double percentageNotTested = ((double) totalCoveredEdges / totalEdges) * 100.0;
        System.out.println("Percentage of method calls not tested: " + percentageNotTested + "%");

        System.out.println("Total number of nodes: " + totalNodes);
        System.out.println("Total number of nodes covered: " + totalCoveredNodes);
        double percentageNotTestedNodes = ((double) totalCoveredNodes / totalNodes) * 100.0;
        System.out.println("Percentage of methods not tested: " + percentageNotTestedNodes + "%");

    }
    

    private static void outputMissingTests(Set<MethodNode> untestedMethods, String outputPath) {
        JSONArray untestedMethodsJson = new JSONArray();
        
        untestedMethods.forEach(method -> {
            JSONObject methodJson = new JSONObject();
            methodJson.put("name", method.getName());
            methodJson.put("class", method.getClassName());
            methodJson.put("type", NodeType.METHOD.toString());
            untestedMethodsJson.put(methodJson);
        });
        
        GraphUtils.writeFile(outputPath, untestedMethodsJson.toString(4).getBytes());
    } 
}
