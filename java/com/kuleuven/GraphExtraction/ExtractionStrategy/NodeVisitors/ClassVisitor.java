package com.kuleuven.GraphExtraction.ExtractionStrategy.NodeVisitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


import java.util.LinkedList;
import java.util.List;

public class ClassVisitor extends VoidVisitorAdapter<Void> {
    private List<ClassOrInterfaceDeclaration> classDefinitions = new LinkedList<>();


    @Override
    public void visit(ClassOrInterfaceDeclaration cid, Void arg) {
        super.visit(cid, arg);
        classDefinitions.add(cid);
    }


    public List<ClassOrInterfaceDeclaration> getDeclaredClasses() {
        return classDefinitions;
    }
}