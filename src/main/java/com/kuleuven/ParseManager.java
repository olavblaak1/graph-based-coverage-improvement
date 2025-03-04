package com.kuleuven;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ParseManager {

    private JavaParser javaParser;
    private List<CompilationUnit> compilationUnits;


    public List<Path> getClasspathJars(Path classPaths) {
        try {
            String classpath = new String(Files.readAllBytes(classPaths));
            return Arrays.stream(classpath.split(File.pathSeparator))
                    .map(Paths::get)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read classpath file", e);
        }
    }

    public void setupParser(List<Path> jarPaths, List<File> srcDirs) {
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver(new ReflectionTypeSolver());

        for (File srcDir : srcDirs) {
            if (srcDir.exists() && srcDir.isDirectory()) {
                combinedSolver.add(new JavaParserTypeSolver(srcDir));
                System.out.println("Added source directory to type solver: " + srcDir.getPath());
            } else {
                System.err.println("Directory does not exist: " + srcDir.getPath());
            }
        }

        for (Path path : jarPaths) {
            try {
                combinedSolver.add(new JarTypeSolver(path));
                System.out.println(("Added jar to type solver: " + path));
            } catch (IOException e) {
                System.err.println("Failed to add jar to type solver: " + path);
                System.err.println("Error message: " + e.getMessage());
            }
        }


        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration().setSymbolResolver(symbolSolver);
        this.javaParser = new JavaParser(parserConfiguration);
        this.compilationUnits = new LinkedList<>();
    }

    public void parseDirectory(File directory) {
        if (directory.isDirectory()) {
            try {
                Files.walk(directory.toPath())
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".java"))
                        .forEach(path -> this.parseJavaFile(path.toFile()));
            } catch (IOException e) {
                System.err.println("Error walking directory: " + directory.getName());
                System.err.println("Error message: " + e.getMessage());
            }
        }
    }

    private void parseJavaFile(File file) {
        try (FileInputStream in = new FileInputStream(file)) {

            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            parseResult.ifSuccessful(cu -> compilationUnits.add(cu));
            if (file.getAbsolutePath().contains("TestLocalTime")) {
                System.out.println("Added " + file.getAbsolutePath());
            }

        } catch (Exception e) {
            System.err.println("Error processing file: " + file.getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<CompilationUnit> getCompilationUnits() {
        return new LinkedList<>(compilationUnits);
    }

    public List<MethodDeclaration> getTestCases() {
        return compilationUnits.stream().flatMap(cu -> cu.findAll(MethodDeclaration.class).stream())
                .filter(method ->
                        method.isAnnotationPresent("org.junit.jupiter.api.Test")
                        || method.getNameAsString().startsWith("test"))
                .filter(method -> !method.isPrivate())
                .collect(Collectors.toList());
    }


    public List<MethodDeclaration> getFilteredTestCases(Path testMethodListPath) throws IOException {
        Set<String> testMethodList = new HashSet<>();
        JSONObject testMethodListJson = new JSONObject(new String(Files.readAllBytes(testMethodListPath)));
        JSONArray testMethods = testMethodListJson.getJSONArray("minimizedTests");
        for (int i = 0; i < testMethods.length(); i++) {
            JSONObject testMethod = testMethods.getJSONObject(i);
            testMethodList.add(testMethod.getString("name"));
        }
        return compilationUnits.stream().flatMap(cu -> cu.findAll(MethodDeclaration.class).stream())
                .filter(method ->
                        method.isAnnotationPresent("org.junit.jupiter.api.Test")
                        || method.getNameAsString().startsWith("test"))
                .filter(method -> !method.isPrivate())
                .filter(method -> testMethodList.contains(method.resolve().getQualifiedName()))
                .collect(Collectors.toList());
    }
}
