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

        // Ask user if they want to randomly mark test cases
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to randomly mark half of the test cases? (yes/no): ");
        String userInput = scanner.nextLine().trim().toLowerCase();

        Set<MethodDeclaration> testMethodsToMark;
        if (userInput.equals("yes")) {
            // Randomly select half of the test cases
            Set<MethodDeclaration> allTestCases = parseManager.getTestCases();
            List<MethodDeclaration> testCaseList = new ArrayList<>(allTestCases);
            Collections.shuffle(testCaseList);
            testMethodsToMark = new HashSet<>(testCaseList.subList(0, testCaseList.size() / 2));
            System.out.println("Randomly selected half of the test cases to mark.");
        } else {
            // Use the usual minimized test cases
            testMethodsToMark = parseManager.getFilteredTestCases(retainedMethodsListPath);
            System.out.println("Using minimized test cases from the provided list.");
        }

        // Mark the test methods
        parseManager.markTestMethodsInSourceRoots(testMethodsToMark);
    }
}