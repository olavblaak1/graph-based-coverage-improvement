package com.kuleuven.TestMinimization;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.GraphUtils;
import com.kuleuven.Graph.Serializer.SerializeManager;
import com.kuleuven.ParseManager;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


/*
    * This class is responsible for minimizing the test suite, it works as follows:
    * 1. It ranks test cases based on the coverage they provide, including the importance of the nodes they cover.
    *       The importance of a node is determined by the number of test cases that cover it and its rank in the graph.
    *       The importance of an edge is determined by the number of test cases that cover it and the importance of the nodes it connects.
    *
    *  The formula used to calculate the importance of a node is:
    *         importance(node) = exp(-number_of_test_cases_covering_node) * rank(node)
    *         OR
    *         importance(node) = exp(-number_of_test_cases_covering_node) * exp(rank(node)) but this would be no different from a linear rank.
    *   Rationale: If a node is covered by many test cases, removing a test case is less likely to affect the coverage of the system, so it is less important.
    *              If a node has a high rank, it is more important to test, so it is better to keep plenty of test-cases.
    *
    *
    *   The formula used to calculate the importance of an edge is:
    *        importance(edge) = exp(-number_of_test_cases_covering_edge) * ((importance(node1) + importance(node2)) / 2)
    *
    *
    *  The importance of a test case is the sum of the importance of the nodes and edges it covers.
    *
    *
    *
    * 2. It returns a list of the most important test cases.
    *
 */
public class Main {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Usage: java TestMinimization <graphPath> <test_directory_path> <jar_path> <src_dir> <testcase_importance>");
            return;
        }
        String graphPath = args[0];
        File testDirectory = new File(args[1]);
        Path jarPath = Paths.get(args[2]);
        File srcDir = new File(args[3]);
        //String outputPath = args[4];
        MinimizationMethod minimizationMethod = MinimizationMethod.valueOf(args[4]);


        JSONObject graphJson = GraphUtils.readGraph(graphPath);

        SerializeManager serializeManager = new SerializeManager();
        RankedGraph<? extends Graph> SUTGraph = serializeManager.deserializeRankedGraph(graphJson);

       ParseManager parseManager = new ParseManager();
        List<Path> jarPaths = parseManager.getClasspathJars(jarPath);
        parseManager.setupParser(jarPaths, srcDir);
        parseManager.parseDirectory(testDirectory);

        TestMinimization testMinimization = new TestMinimization(minimizationMethod);
        Map<MethodDeclaration, Double> rankedTests = testMinimization.minimizeTests((RankedGraph<CoverageGraph>) SUTGraph, parseManager.getTestCases());

        rankedTests.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> System.out.println(entry.getKey().resolve().getQualifiedName() + " : " + entry.getValue()));
        // output to a txt:
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tests.txt"))) {
            rankedTests.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(entry -> {
                        try {
                            writer.write(entry.getKey().resolve().getQualifiedName() + " : " + entry.getValue());
                            writer.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
