package com.kuleuven.GraphExtraction;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.LinkedList;
import java.util.List;

public class MethodVisitor extends VoidVisitorAdapter<String> {
    private final List<MethodDeclaration> methodDeclarations = new LinkedList<>();

    @Override
    public void visit(MethodDeclaration md, String className) {
        super.visit(md, className);
        methodDeclarations.add(md);
    }


    public List<MethodDeclaration> getMethodDeclarations() {
        return methodDeclarations;
    }
}