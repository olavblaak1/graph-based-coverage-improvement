package com.kuleuven.Graph;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GraphUtils {

    public static void writeFile(String outputFilePath, byte[] content) {
        try {
            Path path = Paths.get(outputFilePath);
            Path parentDir = path.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            Files.write(path, content);
            System.out.println("File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads the graph json file and returns the graph object
    public static JSONObject readGraph(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject json = new JSONObject(content);
            json.put("nodes", json.getJSONArray("nodes"));
            json.put("edges", json.getJSONArray("edges"));
            return json;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

