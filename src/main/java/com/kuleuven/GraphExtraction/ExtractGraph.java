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
import java.util.List;

public class ExtractGraph {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: java CollaborationDiagramExtractor <output_json_file_path> <source_directory> <jar_path> <extraction_method> <metric>]");
            return;
        }

        String outputFilePath = args[0];
        File mainDirectory = new File(args[1]);
        Path jarPath = Paths.get(args[2]);
        ExtractionStrategy extractionStrategy = ExtractionStrategy.valueOf(args[3]);

        GraphExtractor extractor = new GraphExtractor(extractionStrategy);
        ParseManager parseManager = new ParseManager();

        List<Path> jarPaths = parseManager.getClasspathJars(jarPath);
        parseManager.setupParser(jarPaths, mainDirectory);
        parseManager.parseDirectory(mainDirectory);

        // Extract the graph from the parsed (Java source files)
        extractor.extractGraph(parseManager.getCompilationUnits());


        GraphSerializer<Graph> graphSerializer = new BasicGraphSerializer();
        JSONObject graph = graphSerializer.serializeGraph(extractor.getGraph());

        GraphUtils.writeFile(outputFilePath, graph.toString(4).getBytes());
        System.out.println("Graph has been saved to " + outputFilePath);
    }

}
