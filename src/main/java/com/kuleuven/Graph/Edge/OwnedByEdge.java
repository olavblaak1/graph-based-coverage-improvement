package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class OwnedByEdge extends Edge {

    public OwnedByEdge(String source, String destination) {
        super(source, destination);
    }

    @Override
    public EdgeType getType() {
        return EdgeType.OWNED_BY;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean isCoveredBy(ResolvedMethodDeclaration methodDeclaration) {
        return methodDeclaration.getQualifiedName().equals(getSource());
    }
}
