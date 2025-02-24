package com.kuleuven;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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

    public void setupParser(List<Path> jarPaths, File mainDirectory) {
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver(new ReflectionTypeSolver());

        if (mainDirectory.exists() && mainDirectory.isDirectory()) {
            combinedSolver.add(new JavaParserTypeSolver(mainDirectory));
        } else {
            System.err.println("Directory does not exist: " + mainDirectory.getPath());
        }

        try {
            for (Path path : jarPaths) {
                combinedSolver.add(new JarTypeSolver(path));
            }
        } catch (IOException e) {
            System.err.println("Failed to load JAR for type resolution: " + e.getMessage());
        }

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
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
                        method.isAnnotationPresent("Test")
                        || method.getNameAsString().startsWith("test"))
                .collect(Collectors.toList());
    }
}
