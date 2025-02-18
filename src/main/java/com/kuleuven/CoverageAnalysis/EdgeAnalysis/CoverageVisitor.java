package com.kuleuven.CoverageAnalysis.EdgeAnalysis;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;

public interface CoverageVisitor<T extends ResolvedDeclaration> {
    boolean isCoveredBy(MethodCallEdge edge, T declaration);

    boolean isCoveredBy(InheritanceEdge edge, T declaration);

    boolean isCoveredBy(FieldEdge edge, T declaration);

    boolean isCoveredBy(OwnedByEdge edge, T declaration);


    boolean isCoveredBy(ClassNode node, T methodCall);

    boolean isCoveredBy(MethodNode node, T methodCall);

    boolean isCoveredBy(OverridesEdge overridesEdge, T declaration);

    boolean isCoveredBy(FieldAccessEdge fieldAccessEdge, T declaration);


}
