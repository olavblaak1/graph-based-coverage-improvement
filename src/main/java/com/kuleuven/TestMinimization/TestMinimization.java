package com.kuleuven.TestMinimization;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.TestMinimization.ImportanceCalculation.*;

import java.util.Collection;
import java.util.Map;

public class TestMinimization {
    private Minimizer minimizer;

    public TestMinimization(ImportanceStrategy importanceStrategy, double discountFactor, double coverageFactor) {
        switch (importanceStrategy) {
            case STATIC:
                minimizer = new Minimizer(new StaticImportanceVisitor(discountFactor, coverageFactor));
                break;
            case EXP_SCORE_BASED:
                minimizer = new Minimizer(new ExpScoreBasedImportanceVisitor());
                break;
            case SCORE_BASED:
                minimizer = new Minimizer(new ScoreBasedImportanceVisitor());
                break;
            case COMBINED:
                minimizer = new Minimizer(new CombinedImportanceVisitor());
                break;
            default:
                break;
        }
    }


    /*
    The private methods are included because they may be structural methods that are called by the test methods,
    these structural method's bodies are considered inlined in the test method.
     */
    public Map<TestCase, Double> minimizeTests(RankedGraph<CoverageGraph> SUTGraph, Collection<MethodDeclaration> testMethods, Collection<MethodDeclaration> privateMethods) {
        Map<TestCase, Double> result;
        result = minimizer.minimizeTests(new RankedGraph<>(SUTGraph), testMethods, privateMethods);
        if (result.size() != testMethods.size()) {
            System.out.println("Warning: The number of test methods after minimization is not equal to the number of test methods before minimization.");
            System.out.println("Before minimization: " + testMethods.size() + " test methods.");
            System.out.println("After minimization: " + result.size() + " test methods.");

            System.out.println("The following test methods were not found after minimization:");
            for (MethodDeclaration testCase : testMethods) {
                if (!result.containsKey(testCase)) {
                    System.out.println("Test method not found: " + testCase.resolve().getQualifiedName());
                }
            }
        }
        return result;
    }
}
