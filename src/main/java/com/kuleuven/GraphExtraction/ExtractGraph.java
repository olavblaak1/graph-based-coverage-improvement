package com.kuleuven.GraphExtraction;

import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.GraphUtils;
import com.kuleuven.Graph.Serializer.Graph.BasicGraphSerializer;
import com.kuleuven.Graph.Serializer.Graph.GraphSerializer;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.Metric;
import com.kuleuven.GraphAnalyzer.NodeRanker;
import com.kuleuven.GraphExtraction.ExtractionStrategy.ExtractionStrategy;
import com.kuleuven.ParseManager;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ExtractGraph {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java CollaborationDiagramExtractor <systemName> <extractionStrategy>");
            return;
        }
        String systemName = args[0];

        String outputFilePath = "data/" + systemName + "/graph/graph.json";
        File mainDirectory = new File("systems/" + systemName + "/src/main/java");
        Path classPaths = Paths.get("systems/" + systemName + "/target/classpath.txt");
        ExtractionStrategy extractionStrategy = ExtractionStrategy.valueOf(args[1]);
        Path jarPath = Paths.get("systems/" + systemName + "/target/targetjars.txt");

        GraphExtractor extractor = new GraphExtractor(extractionStrategy);
        ParseManager parseManager = new ParseManager();

        List<Path> dependencyPaths = parseManager.getClasspathJars(classPaths);
        List<Path> compiledJarsPaths = parseManager.getClasspathJars(jarPath);

        List<Path> jarPaths = new LinkedList<>();
        jarPaths.addAll(dependencyPaths);
        jarPaths.addAll(compiledJarsPaths);
        parseManager.setupParser(jarPaths, List.of(mainDirectory));
        parseManager.parseDirectory(mainDirectory);

        // Extract the graph from the parsed (Java source files)
        extractor.extractGraph(parseManager.getCompilationUnits());


        GraphSerializer<Graph> graphSerializer = new BasicGraphSerializer();
        JSONObject graph = graphSerializer.serializeGraph(extractor.getGraph());

        GraphUtils.writeFile(outputFilePath, graph.toString(4).getBytes());
        System.out.println("Graph has been saved to " + outputFilePath);
    }

}
