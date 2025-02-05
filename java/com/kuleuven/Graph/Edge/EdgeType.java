package com.kuleuven.Graph.Edge;

public enum EdgeType {
    METHOD_CALL,
    INHERITANCE,
    FIELD, METHOD_OWN;

    public String toString() {
        switch(this) {
            case METHOD_CALL: return "MethodCall";
            case INHERITANCE: return "Inheritance";
            case FIELD: return "Field";
        }
        return null;
    }
}
