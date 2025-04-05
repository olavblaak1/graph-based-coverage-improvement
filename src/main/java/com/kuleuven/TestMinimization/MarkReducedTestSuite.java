package com.kuleuven.TestMinimization;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.kuleuven.ParseManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class MarkReducedTestSuite {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java MarkReducedTestSuite <systemName>");
            return;
        }

        System.out.println("Note: This will only work for JUnit 5 or above");

        String systemName = args[0];
        File testDirectory = new File("systems/" + systemName + "/src/test/java");
        Path classPaths = Paths.get("systems/" + systemName + "/target/classpath.txt");
        File srcDir = new File("systems/" + systemName + "/src/main/java");
        Path jarPath = Paths.get("systems/" + systemName + "/target/targetjars.txt");


        Path retainedMethodsListPath = Paths.get("data/" + systemName + "/minimization/minimizedTests.json");
        ParseManager parseManager = new ParseManager();

        // We just want to mark all tests, so no jars necessary
        parseManager.setupParser(List.of(classPaths, jarPath), List.of(testDirectory));
        parseManager.parseDirectory(testDirectory);
        Set<MethodDeclaration> minimizedTests = parseManager.getFilteredTestCases(retainedMethodsListPath);

        parseManager.markTestMethodsInSourceRoots(minimizedTests);
    }
}