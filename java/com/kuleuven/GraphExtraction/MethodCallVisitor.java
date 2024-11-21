package com.kuleuven.GraphExtraction;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.LinkedList;
import java.util.List;

public class MethodCallVisitor extends VoidVisitorAdapter<Void> {
    private final LinkedList<MethodCallExpr> methodCalls = new LinkedList<>();

    @Override
    public void visit(MethodCallExpr mce, Void arg) {
        super.visit(mce, arg);
        methodCalls.add(mce);
    }

    public List<MethodCallExpr> getMethodCalls() {
        return methodCalls;
    }
}