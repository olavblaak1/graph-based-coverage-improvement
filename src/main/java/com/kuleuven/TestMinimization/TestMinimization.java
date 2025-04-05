package com.kuleuven.TestMinimization;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.TestMinimization.ImportanceCalculation.MinimizationImportanceVisitor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestMinimization {
    private Minimizer minimizer;

    public TestMinimization(MinimizationMethod minimizationMethod, double discountFactor, double coverageFactor) {
        switch (minimizationMethod) {
            case STANDARD:
                minimizer = new Minimizer(new MinimizationImportanceVisitor(discountFactor, coverageFactor));
                break;
            default:
                break;
        }
    }


    public Map<MethodDeclaration, Double> minimizeTests(RankedGraph<CoverageGraph> SUTGraph, Collection<MethodDeclaration> testClasses) {
        return minimizer.minimizeTests(new RankedGraph<>(SUTGraph), testClasses);
    }
}
