package com.kuleuven.GraphExtraction;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
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


    public static String getUniqueId(MethodCallExpr methodCallExpr, MethodDeclaration sourceMethodDeclaration, String className) {
        return className
                + "->" + 
                methodCallExpr.resolve().declaringType().getQualifiedName()  
                + ":" +
                methodCallExpr.resolve().getSignature() 
                + "@" +
                methodCallExpr.resolve().getSignature();

    }
}

