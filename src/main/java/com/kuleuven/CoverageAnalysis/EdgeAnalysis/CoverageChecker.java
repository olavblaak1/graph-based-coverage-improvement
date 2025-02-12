package com.kuleuven.CoverageAnalysis.EdgeAnalysis;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.kuleuven.Graph.Edge.FieldEdge;
import com.kuleuven.Graph.Edge.InheritanceEdge;
import com.kuleuven.Graph.Edge.MethodCallEdge;
import com.kuleuven.Graph.Edge.OwnedByEdge;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.Node;

public class CoverageChecker implements CoverageVisitor {


    @Override
    public boolean isCoveredBy(MethodCallEdge edge, ResolvedMethodDeclaration methodDeclaration) {
        Node dest = edge.getDestination();
        String untestedName = dest.getName();

        String testedMethodName = methodDeclaration.getQualifiedName();
        String testedSignature = methodDeclaration.getSignature();

        if (dest instanceof MethodNode) {
            String untestedSignature = ((MethodNode) dest).getSignature();
            return untestedName.equals(testedMethodName) &&
                    untestedSignature.equals(testedSignature);
        }
        else if (dest instanceof ClassNode) {
            return untestedName.equals(testedMethodName);
        }
        else {
            throw new IllegalArgumentException("Unrecognized node: " + dest);
        }
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
}
