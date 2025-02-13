package com.kuleuven.CoverageAnalysis.EdgeAnalysis;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;

public interface CoverageVisitor {
    boolean isCoveredBy(MethodCallEdge edge, ResolvedMethodDeclaration methodDeclaration);

    boolean isCoveredBy(InheritanceEdge edge, ResolvedMethodDeclaration methodDeclaration);

    boolean isCoveredBy(FieldEdge edge, ResolvedMethodDeclaration methodDeclaration);

    boolean isCoveredBy(OwnedByEdge edge, ResolvedMethodDeclaration methodDeclaration);


    boolean isCoveredBy(ClassNode node, ResolvedMethodDeclaration methodCall);

    boolean isCoveredBy(MethodNode node, ResolvedMethodDeclaration methodCall);

    boolean isCoveredBy(OverridesEdge overridesEdge, ResolvedMethodDeclaration methodDeclaration);

    boolean isCoveredBy(FieldAccessEdge fieldAccessEdge, ResolvedMethodDeclaration methodDeclaration);
}
