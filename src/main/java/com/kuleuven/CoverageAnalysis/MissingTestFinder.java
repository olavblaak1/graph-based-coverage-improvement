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


        int totalEdges = coverageGraph.getEdges().size();
        int totalNodes = coverageGraph.getNodes().size();

        int totalCoveredEdges = (int) coverageGraph.getEdges().stream().filter(
                coverageGraph::isEdgeMarked).count();
        int totalCoveredNodes = (int) coverageGraph.getNodes().stream().filter(
                coverageGraph::isNodeMarked).count();


        System.out.println("Total number of nodes: " + totalNodes);
        System.out.println("Total number of nodes covered: " + totalCoveredNodes);
        double percentageTestedNodes = ((double) totalCoveredNodes / totalNodes);
        System.out.println("Percentage of nodes tested: " + percentageTestedNodes);


        System.out.println("Total number of edges: " + totalEdges);
        System.out.println("Total number of edges covered: " + totalCoveredEdges);
        System.out.println("Percentage of edges covered: " + totalCoveredEdges / totalEdges);

        System.out.println("Total number of overrides edges: " + coverageGraph.getEdgesOfType(EdgeType.OVERRIDES).size());
        double overridesCovered = coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.OVERRIDES);
        System.out.println("Percentage of overrides edges covered: " + overridesCovered);

        System.out.println("Total number of field access edges: " + coverageGraph.getEdgesOfType(EdgeType.FIELD_ACCESS).size());
        double fieldAccessCovered = coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.FIELD_ACCESS);
        System.out.println("Percentage of field access edges covered: " + fieldAccessCovered);

        System.out.println("Total number of method call edges: " + coverageGraph.getEdgesOfType(EdgeType.METHOD_CALL).size());
        double methodCallsCovered = coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.METHOD_CALL);
        System.out.println("Percentage of method calls edges covered: " + methodCallsCovered);

        System.out.println("Total number of owned_by edges: " + coverageGraph.getEdgesOfType(EdgeType.OWNED_BY).size());
        double ownedByCovered = coverageGraph.getEdgeTypeCoveragePercentage(EdgeType.OWNED_BY);
        System.out.println("Percentage of owned by edges covered: " + ownedByCovered);


        GraphSerializer<CoverageGraph> coverageGraphSerializer = new CoverageGraphSerializer();
        JSONObject coverageGraphJson = coverageGraphSerializer.serializeGraph(coverageGraph);

        GraphUtils.writeFile(outputPath, coverageGraphJson.toString(4).getBytes());
    }
}
