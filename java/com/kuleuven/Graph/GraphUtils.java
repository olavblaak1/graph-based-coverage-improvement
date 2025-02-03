package com.kuleuven.Graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GraphUtils {

    public static void writeFile(String outputFilePath, byte[] content) {
        try {
            Path path = Path.of(outputFilePath);
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
}

