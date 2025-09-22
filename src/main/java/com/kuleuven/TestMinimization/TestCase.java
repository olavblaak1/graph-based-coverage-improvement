package com.kuleuven.TestMinimization;

import com.github.javaparser.ast.body.MethodDeclaration;

public class TestCase {
    private final MethodDeclaration original;

    public TestCase(MethodDeclaration original) {
        this.original = original;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestCase)) return false;
        MethodDeclaration other = ((TestCase) o).original;

        return original.resolve().getQualifiedSignature().equalsIgnoreCase(other.resolve().getQualifiedSignature());
    }

    @Override
    public int hashCode() {
        return original.resolve().getQualifiedName().hashCode();
    }

    public MethodDeclaration getOriginal() {
        return original;
    }
}