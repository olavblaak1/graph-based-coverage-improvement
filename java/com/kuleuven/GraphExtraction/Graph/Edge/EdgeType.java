package com.kuleuven.GraphExtraction.Graph.Edge;

public enum EdgeType {
    METHOD_CALL;

    public String toString() {
        switch(this) {
            case METHOD_CALL: return "MethodCall";
        }
        return null;
    }
}
