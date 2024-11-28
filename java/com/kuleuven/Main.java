package com.kuleuven;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

import com.kuleuven.GraphExtraction.ExtractionStrategy;
import com.kuleuven.GraphExtraction.GraphExtractor;
import com.kuleuven.GraphExtraction.GraphUtils;
import com.kuleuven.GraphExtraction.Graph.Serializer.ClassNodeSerializer;
import com.kuleuven.GraphExtraction.Graph.Serializer.EdgeSerializer;
import com.kuleuven.GraphExtraction.Graph.Serializer.MethodCallEdgeSerializer;
import com.kuleuven.GraphExtraction.Graph.Serializer.NodeSerializer;
import com.kuleuven.GraphExtraction.Graph.Serializer.SerializeManager;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java CollaborationDiagramExtractor <output_json_file_path> <source_directory> <jar_path>]");
            return;
        }

        String outputFilePath = args[0];
        File mainDirectory = new File(args[1]);
        Path jarPath = Paths.get(args[2]);
        SerializeManager serializeManager = new SerializeManager();

        GraphExtractor extractor = new GraphExtractor(ExtractionStrategy.ORIGINAL);
        extractor.setupParser(jarPath, mainDirectory);
        
        // TODO: CollectClassNames removed, check if that is a problem
        // It is not, but it now includes edges between classes that were imported and classes that were defined in the same file
        if (mainDirectory.isDirectory()) {
            Files.walk(mainDirectory.toPath())
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> extractor.parseJavaFile(path.toFile()));
        }

        JSONObject graph = new JSONObject();

        graph.put("nodes", serializeManager.serializeNodes(extractor.getNodes()));
        graph.put("edges", serializeManager.serializeEdges(extractor.getEdges()));

        GraphUtils.writeFile(outputFilePath, graph.toString(4).getBytes());
        System.out.println("Graph has been saved to " + outputFilePath);
    }

}
