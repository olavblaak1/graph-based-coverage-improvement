package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.resolution.TypeSolver;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph;

import java.util.List;

public class CoverageAnalyzer {
    private final CoverageTemplate strategy;


    public CoverageAnalyzer(AnalysisStrategy strategy) {
        switch (strategy) {
            case METHODS:
                this.strategy = new MethodCoverageStrategy();
                break;
            default:
                throw new IllegalArgumentException("Invalid analysis strategy: " + strategy);
        }
    }

    public CoverageGraph analyze(List<CompilationUnit> cus, Graph SUTGraph) {
        return strategy.analyze(cus, SUTGraph);
    }
}
