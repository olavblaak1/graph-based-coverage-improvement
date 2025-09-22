package com.kuleuven.CoverageAnalysis.EdgeAnalysis;

import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;

public class FieldCoverageChecker implements CoverageVisitor<ResolvedFieldDeclaration> {

    @Override
    public boolean isCoveredBy(MethodCallEdge edge, ResolvedFieldDeclaration fieldDeclaration) {
        return false;
    }

    @Override
    public boolean isCoveredBy(InheritanceEdge edge, ResolvedFieldDeclaration fieldDeclaration) {
        return false;
    }

    @Override
    public boolean isCoveredBy(FieldEdge edge, ResolvedFieldDeclaration fieldDeclaration) {
        return false;
    }

    @Override
    public boolean isCoveredBy(OwnedByEdge edge, ResolvedFieldDeclaration fieldDeclaration) {
        return false;
    }

    @Override
    public boolean isCoveredBy(ClassNode node, ResolvedFieldDeclaration fieldDeclaration) {
        String accessedFieldName = node.getName();
        String fieldNameToCheck = fieldDeclaration.getName();

        return fieldNameToCheck.equals(accessedFieldName);
    }

    @Override
    public boolean isCoveredBy(MethodNode node, ResolvedFieldDeclaration fieldDeclaration) {
        return false;
    }

    @Override
    public boolean isCoveredBy(OverridesEdge overridesEdge, ResolvedFieldDeclaration fieldDeclaration) {
        return false;
    }

    @Override
    public boolean isCoveredBy(FieldAccessEdge fieldAccessEdge, ResolvedFieldDeclaration fieldDeclaration) {
        String accessedFieldName = fieldAccessEdge.getDestination().getName();
        String fieldNameToCheck = fieldDeclaration.getName();

        return fieldNameToCheck.equals(accessedFieldName);
    }
}
