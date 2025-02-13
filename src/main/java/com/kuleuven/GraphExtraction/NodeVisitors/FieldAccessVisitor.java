package com.kuleuven.GraphExtraction.NodeVisitors;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.LinkedList;
import java.util.List;

public class FieldAccessVisitor extends VoidVisitorAdapter<Void> {
    private final LinkedList<FieldAccessExpr> accessExprs = new LinkedList<>();

    @Override
    public void visit(FieldAccessExpr mce, Void arg) {
        super.visit(mce, arg);
        accessExprs.add(mce);
    }

    public List<FieldAccessExpr> getAccessExprs() {
        return accessExprs;
    }
}
