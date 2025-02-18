package com.kuleuven.CoverageAnalysis.EdgeAnalysis;

import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;

public class MethodCoverageChecker implements CoverageVisitor<ResolvedMethodDeclaration> {


    @Override
    public boolean isCoveredBy(MethodCallEdge edge, ResolvedMethodDeclaration methodDeclaration) {
        return false;
    }

    @Override
    public boolean isCoveredBy(InheritanceEdge edge, ResolvedMethodDeclaration methodDeclaration) {
        return false;
    }

    @Override
    public boolean isCoveredBy(FieldEdge edge, ResolvedMethodDeclaration methodDeclaration) {
        return false;
    }

    @Override
    public boolean isCoveredBy(OwnedByEdge edge, ResolvedMethodDeclaration methodDeclaration) {
        return false;
    }

    // Nodes
    @Override
    public boolean isCoveredBy(ClassNode node, ResolvedMethodDeclaration methodCall) {
        return false;
    }

    @Override
    public boolean isCoveredBy(MethodNode node, ResolvedMethodDeclaration methodCall) {
        String untestedMethodName = node.getName();
        String untestedSignature = node.getSignature();

        String testedMethodName = methodCall.getQualifiedName();
        String testedSignature = methodCall.getSignature();

        return untestedMethodName.equals(testedMethodName) &&
                untestedSignature.equals(testedSignature);
    }

    @Override
    public boolean isCoveredBy(OverridesEdge overridesEdge, ResolvedMethodDeclaration methodDeclaration) {
        return false;
    }

    @Override
    public boolean isCoveredBy(FieldAccessEdge fieldAccessEdge, ResolvedMethodDeclaration declaration) {
        return false;
    }
}
