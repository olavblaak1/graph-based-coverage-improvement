package com.kuleuven.CoverageAnalysis;

import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.GraphUtils;
import com.kuleuven.Graph.Serializer.Graph.BasicGraphSerializer;
import com.kuleuven.Graph.Serializer.Graph.CoverageGraphSerializer;
import com.kuleuven.Graph.Serializer.Graph.GraphSerializer;
import com.kuleuven.ParseManager;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MissingTestFinder {

    public static void main(String[] args) {
        if (args.length < 6) {
            System.err.println("Usage: java MissingTestFinder <graphPath> <test_directory_path> <jar_path> <src_dir> <analysisStrategy> <output_graph>");
            return;
        }
        String graphPath = args[0];
        File testDirectory = new File(args[1]);
        Path classPaths = Paths.get(args[2]);
        File srcDir = new File(args[3]);
        AnalysisMethod analysisMethod = AnalysisMethod.valueOf(args[4]);
        String outputPath = args[5];


        JSONObject graphJson = GraphUtils.readGraph(graphPath);

        GraphSerializer<Graph> graphSerializer = new BasicGraphSerializer();
        Graph SUTGraph = graphSerializer.deserializeGraph(graphJson);

        ParseManager parseManager = new ParseManager();
        List<Path> jarPaths = parseManager.getClasspathJars(classPaths);

        parseManager.setupParser(jarPaths, srcDir);
        parseManager.parseDirectory(testDirectory);


        CoverageAnalyzer coverageAnalyzer = new CoverageAnalyzer(analysisMethod);
        CoverageGraph coverageGraph = coverageAnalyzer.analyze(parseManager.getTestCases(), SUTGraph);


        GraphSerializer<CoverageGraph> coverageGraphSerializer = new CoverageGraphSerializer();
        JSONObject coverageGraphJson = coverageGraphSerializer.serializeGraph(coverageGraph);
        AnalysisResult analysisResult = new AnalysisResult(coverageGraph);
        JSONObject analysisResults = analysisResult.toJson();

        GraphUtils.writeFile(outputPath + "/coverageGraph.json", coverageGraphJson.toString(4).getBytes());
        GraphUtils.writeFile(outputPath + "/coverageAnalysisResults.json", analysisResults.toString(4).getBytes());
    }
}
