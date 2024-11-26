package com.kuleuven.GraphExtraction.Graph.Edge;

import java.util.List;

public class Method {
    private String name;
    private String returnType;
    private String declaringClass;
    private String signature;
    private List<Argument> arguments;

    public Method(String name, String returnType, String declaringClass, String signature, List<Argument> arguments) {
        this.name = name;
        this.returnType = returnType;
        this.declaringClass = declaringClass;
        this.signature = signature;
        this.arguments = arguments;
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

    public List<Argument> getArguments() {
        return arguments;
    }
}
