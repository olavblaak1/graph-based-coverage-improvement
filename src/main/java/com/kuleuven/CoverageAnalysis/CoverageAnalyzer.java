package com.kuleuven.CoverageAnalysis;

import com.github.javaparser.ast.CompilationUnit;
import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Graph.Graph;

import java.util.List;

public class CoverageAnalyzer {
    private final Coverage strategy;


    public CoverageAnalyzer(AnalysisMethod strategy) {
        switch (strategy) {
            case METHODS:
                this.strategy = new MethodGraphCoverage();
                break;
            case FULL:
                this.strategy = new FullGraphCoverage();
                break;
            default:
                throw new IllegalArgumentException("Invalid analysis strategy: " + strategy);
        }
    }

    public CoverageGraph analyze(List<CompilationUnit> cus, Graph SUTGraph) {
        return strategy.analyze(cus, SUTGraph);
    }
}
