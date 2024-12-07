package com.kuleuven.GraphExtraction.ExtractionStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class ParseManager {
    
    private JavaParser javaParser;
    private List<CompilationUnit> compilationUnits;

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
        this.javaParser = new JavaParser(parserConfiguration);
        this.compilationUnits = new LinkedList<>();
    }


    public void parseJavaFile(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            parseResult.ifSuccessful(cu -> {
                compilationUnits.add(cu);
            });
        } catch (Exception e) {
            System.err.println("Error processing file: " + file.getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<CompilationUnit> getCompilationUnits() {
        return new LinkedList<>(compilationUnits);
    }
}
