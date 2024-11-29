package com.kuleuven.GraphExtraction.ExtractionStrategy;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.kuleuven.GraphExtraction.Graph.Node;
import com.kuleuven.GraphExtraction.Graph.Edge.Edge;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class GraphExtractor {

    private JavaParser javaParser;
    private List<Node> nodes;
    private List<Edge> edges;

    /*
     * Extraction strategy to use for extracting the graph from the Java source file
     * This dictates whether the nodes are class definitions, method definitions, ...
     * and if the edges are method calls, inheritence relations, ...
     */
    private ExtractionTemplate extractionTemplate;

    public GraphExtractor(ExtractionStrategy strategy) {
        switch (strategy) {
            case ORIGINAL:
                extractionTemplate = new ExtractGraphOriginal();
                break;
            case INHERITANCE_FIELDS:
                extractionTemplate = new ExtractGraphInheritanceFields();
                break;
            default:
                extractionTemplate = new ExtractGraphOriginal();
        }
        nodes = new LinkedList<>();
        edges = new LinkedList<>();
    }

    public void setupParser(Path jarPath, File mainDirectory) {
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        
        if (mainDirectory.exists() && mainDirectory.isDirectory()) {
            combinedSolver.add(new JavaParserTypeSolver(mainDirectory));
        } else {
            System.err.println("Directory does not exist: " + mainDirectory.getPath());
        }

        try {
            combinedSolver.add(new JarTypeSolver(jarPath));
            combinedSolver.add(new JarTypeSolver("target/libs/javax.servlet-api-4.0.1.jar"));
            combinedSolver.add(new JarTypeSolver("target/libs/xercesImpl-2.8.0.jar"));
            combinedSolver.add(new JarTypeSolver("target/libs/xml-apis-1.3.03.jar"));
        } catch (IOException e) {
            System.err.println("Failed to load JAR for type resolution: " + e.getMessage());
        }        

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration().setSymbolResolver(symbolSolver);
        javaParser = new JavaParser(parserConfiguration);
    }


    public void parseJavaFile(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            parseResult.ifSuccessful(cu -> {
                extractionTemplate.extractGraph(cu, edges, nodes);
            });
        } catch (Exception e) {
            System.err.println("Error processing file: " + file.getName());
        }
    }

    public List<Node> getNodes() {
        return new LinkedList<>(nodes);
    }

    public List<Edge> getEdges() {
        return new LinkedList<>(edges);
    }
}