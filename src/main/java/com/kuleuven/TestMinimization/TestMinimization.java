package com.kuleuven.TestMinimization;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;

import java.util.List;
import java.util.Map;

public class TestMinimization {
    private Minimizer minimizer;

    public TestMinimization(MinimizationMethod minimizationMethod) {
        switch (minimizationMethod) {
            case STANDARD:
                minimizer = new Minimizer(new StandardTestCaseVisitor());
                break;
            default:
                break;
        }
    }


    public Map<MethodDeclaration, Double> minimizeTests(RankedGraph<CoverageGraph> SUTGraph, List<MethodDeclaration> testClasses) {
        return minimizer.minimizeTests(new RankedGraph<>(SUTGraph), testClasses);
    }
}
