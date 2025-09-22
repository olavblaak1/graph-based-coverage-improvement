package com.kuleuven.TestMinimization;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.kuleuven.ParseManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MarkReducedTestSuite {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: java MarkReducedTestSuite <systemName> <reductionPercentage> <markRandomly>");
            return;
        }

        System.out.println("Note: This will only work for JUnit 5 or above");

        String systemName = args[0];
        int percentage = Integer.parseInt(args[1]);
        boolean markRandomly = Boolean.parseBoolean(args[2]);
        File testDirectory = new File("systems/" + systemName + "/src/test/java");
        Path classPaths = Paths.get("systems/" + systemName + "/target/classpath.txt");
        Path jarPath = Paths.get("systems/" + systemName + "/target/targetjars.txt");


        Path retainedMethodsListPath = Paths.get("data/" + systemName + "/minimization/minimizedTests.json");
        ParseManager parseManager = new ParseManager();

        List<Path> jarPaths = new ArrayList<>();
        jarPaths.addAll(parseManager.getClasspathJars(classPaths));
        jarPaths.addAll(parseManager.getClasspathJars(jarPath));

        // We just want to mark all tests, so no jars necessary
        parseManager.setupParser(jarPaths, List.of(testDirectory));
        parseManager.parseDirectory(testDirectory);

        Collection<String> testMethodsToMark;
        if (markRandomly) {
            // Randomly select half of the test cases
            Collection<MethodDeclaration> allTestCases = parseManager.getNonPrivateTestCases();
            List<MethodDeclaration> testCaseList = new ArrayList<>(allTestCases);
            Collections.shuffle(testCaseList);

            throw new RuntimeException("Marking randomly has been disabled for now. ");
            //testMethodsToMark = new HashSet<>(testCaseList.subList(0,  (int) (((double) percentage / 100) * testCaseList.size())));
            // System.out.println("Randomly selected half of the test cases to mark.");
        } else {
            // Use the usual minimized test cases
            testMethodsToMark = parseManager.getFilteredTestCases(retainedMethodsListPath, percentage);
            System.out.println("Using minimized test cases from the provided list.");
        }

        // Mark the test methods
        parseManager.markTestMethodsInSourceRoots(testMethodsToMark);
    }
}