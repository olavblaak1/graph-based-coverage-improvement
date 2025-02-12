package com.kuleuven.CoverageAnalysis.MarkVisitor;

import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.MethodNode;

public interface MarkVisitor {
    void mark(MethodCallEdge edge, CoverageGraph graph);

    void mark(InheritanceEdge edge, CoverageGraph graph);

    void mark(FieldEdge edge, CoverageGraph graph);

    void mark(OwnedByEdge edge, CoverageGraph graph);


    void mark(ClassNode node, CoverageGraph graph);

    void mark(MethodNode node, CoverageGraph graph);

    void mark(OverridesEdge overridesEdge, CoverageGraph graph);
}
