package com.kuleuven.GraphExtraction;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.Serializer.BasicGraphSerializer;
import com.kuleuven.Graph.Serializer.GraphSerializer;
import org.json.JSONObject;

import com.kuleuven.ParseManager;
import com.kuleuven.Graph.GraphUtils;
import com.kuleuven.GraphAnalyzer.NodeRanker;
import com.kuleuven.GraphAnalyzer.MetricAnalyzer.Metric.Metric;
import com.kuleuven.GraphExtraction.ExtractionStrategy.ExtractionStrategy;

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
        Metric metric = Metric.valueOf(args[4]);

        GraphExtractor extractor =          new GraphExtractor(extractionStrategy);
        NodeRanker nodeRanker =             new NodeRanker(metric);
        ParseManager parseManager =         new ParseManager();

        List<Path> jarPaths = Arrays.asList(jarPath,
                                    Paths.get("target/libs/javax.servlet-api-4.0.1.jar"), 
                                    Paths.get("target/libs/xercesImpl-2.8.0.jar"), 
                                    Paths.get("target/libs/xml-apis-1.3.03.jar"));
        parseManager.setupParser(jarPaths, mainDirectory);
        parseManager.parseDirectory(mainDirectory);

        // Extract the graph from the parsed (Java source files)
        extractor.extractGraph(parseManager.getCompilationUnits());


        GraphSerializer<Graph> graphSerializer = new BasicGraphSerializer();
        JSONObject graph = graphSerializer.serializeGraph(extractor.getGraph());

        GraphUtils.writeFile(outputFilePath, graph.toString(4).getBytes()); 
        System.out.println("Graph has been saved to " + outputFilePath);
        /*
        Commented out for now

        System.out.println("Calculating " + metric + " metric...");
        
        List<RankedNode> rankedNodes = nodeRanker.rankNodes(extractor.getNodes(), extractor.getEdges());

        JSONObject rankedGraph = new JSONObject();
        rankedGraph.put("nodes", serializeManager.serializeRankedNodes(rankedNodes));
        rankedGraph.put("edges", serializeManager.serializeEdges(extractor.getEdges()));

       GraphUtils.writeFile(outputFilePath.replace(".json", "_ranked.json"), rankedGraph.toString(4).getBytes());
       System.out.println("RankedGraph has been saved to " + outputFilePath.replace(".json", "_ranked.json"));
        */
        

    }

}
