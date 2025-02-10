package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class MethodCallEdge extends Edge {


    public MethodCallEdge(String source, String destination) {
        super(source, destination);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.METHOD_CALL;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean isCoveredBy(ResolvedMethodDeclaration methodDeclaration) {
        return false;
        // TODO: Implement, see MethodCoverageStrategy
    }
}