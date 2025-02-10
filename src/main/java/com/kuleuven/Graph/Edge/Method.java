package com.kuleuven.Graph.Edge;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import java.util.LinkedList;
import java.util.List;

public class Method {
    private final String name;
    private final String returnType;
    private final String declaringClass;
    private final String signature;
    private final List<String> parameters;

    public Method(String name, String returnType, String declaringClass, String signature, List<String> parameters) {
        this.name = name;
        this.returnType = returnType;
        this.declaringClass = declaringClass;
        this.signature = signature;
        this.parameters = parameters;
    }

    // I could make a factory for this, but I'm not sure if it's worth it
    // since the constructors are already pretty simple
    public Method(ResolvedMethodDeclaration methodDeclaration) {
        this.name = methodDeclaration.getName();
        this.returnType = methodDeclaration.getReturnType().describe();
        this.declaringClass = methodDeclaration.declaringType().getQualifiedName();
        this.signature = methodDeclaration.getSignature();
        this.parameters = getParameterNames(methodDeclaration);
    }

    private static List<String> getParameterNames(ResolvedMethodDeclaration methodDeclaration) {
        List<String> parameters = new LinkedList<>();
        methodDeclaration.getTypeParameters().forEach(param -> {
            parameters.add(param.getName());
        });
        return parameters;
    }


    public String getMethodName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    public String getMethodSignature() {
        return signature;
    }

    public List<String> getParameters() {
        return parameters;
    }

}
