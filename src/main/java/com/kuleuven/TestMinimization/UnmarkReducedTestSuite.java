package com.kuleuven.TestMinimization;

import com.kuleuven.ParseManager;

import java.io.File;
import java.util.List;

public class UnmarkReducedTestSuite {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java MarkReducedTestSuite <systemName>");
            return;
        }

        String systemName = args[0];
        File testDirectory = new File("systems/" + systemName + "/src/test/java");
        ParseManager parseManager = new ParseManager();
        parseManager.setupParser(List.of(), List.of(testDirectory));
        parseManager.parseDirectory(testDirectory);
        parseManager.unmarkTestMethods();
    }
}
