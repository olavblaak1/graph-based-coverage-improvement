package com.kuleuven.GraphExtraction;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public static JSONArray getArguments(MethodDeclaration md) {
        JSONArray arguments = new JSONArray();
        md.getParameters().forEach(param -> {
            JSONObject argument = new JSONObject();
            String typeName = param.getType().resolve().describe();
            argument.put("type", typeName);
            argument.put("value", param.getNameAsString());
            arguments.put(argument);
        });
        return arguments;
    }

    public static JSONArray getArguments(MethodCallExpr mce) {
        JSONArray arguments = new JSONArray();
        mce.getArguments().forEach(arg -> {
            JSONObject argument = new JSONObject();
            String typeName = arg.calculateResolvedType().describe();
            argument.put("type", typeName);
            argument.put("value", arg.toString());
            arguments.put(argument);
        });
        return arguments;
    }

    public static JSONObject getMethodJSON(MethodDeclaration mcd, String className) {
        JSONObject method = new JSONObject();
        method.put("method_signature", mcd.getSignature());
        method.put("method_name", mcd.getNameAsString());
        method.put("return_type", mcd.getType().resolve().describe());
        method.put("arguments", getArguments(mcd));
        method.put("declaring_class", className);
        return method;
    }


    public static JSONObject getMethodCallJSON(MethodCallExpr methodCallExpr, String className) {
        JSONObject methodCall = new JSONObject();
        methodCall.put("method_signature", methodCallExpr.resolve().getSignature());
        methodCall.put("method_name", methodCallExpr.getNameAsString());
        methodCall.put("arguments", getArguments(methodCallExpr));
        methodCall.put("return_type", methodCallExpr.calculateResolvedType().describe());
        methodCall.put("declaring_class", methodCallExpr.resolve().declaringType().getQualifiedName());
        return methodCall;
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

