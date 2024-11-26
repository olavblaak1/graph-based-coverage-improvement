package com.kuleuven.GraphExtraction.Graph.Serializer;

import java.util.List;

import org.json.JSONObject;

import com.kuleuven.GraphExtraction.Graph.Edge.Argument;
import com.kuleuven.GraphExtraction.Graph.Edge.Edge;
import com.kuleuven.GraphExtraction.Graph.Edge.MethodCallEdge;
import com.kuleuven.GraphExtraction.Graph.Edge.Method;

public class MethodCallEdgeSerializer implements EdgeSerializer {

    @Override
    public JSONObject serialize(Edge edge) {
        if (!(edge instanceof MethodCallEdge)) {
            throw new IllegalArgumentException("MethodCallEdgeSerializer can only serialize MethodCallEdges");
        }
        MethodCallEdge methodCallEdge = (MethodCallEdge) edge;
        JSONObject json = new JSONObject();
        json.put("source", methodCallEdge.getSource().getName());
        json.put("destination", methodCallEdge.getDestination().getName());

        Method linkMethod = methodCallEdge.getLinkMethod();
        Method sourceMethod = methodCallEdge.getSourceMethod();

        JSONObject linkMethodJSON = new JSONObject();
        linkMethodJSON.put("method_signature", linkMethod.getMethodSignature());
        linkMethodJSON.put("method_name", linkMethod.getMethodName());
        linkMethodJSON.put("return_type", linkMethod.getReturnType());
        linkMethodJSON.put("arguments", serializeArguments(linkMethod.getArguments()));
        linkMethodJSON.put("declaring_class", linkMethod.getDeclaringClass());
        
        JSONObject sourceMethodJSON = new JSONObject();
        sourceMethodJSON.put("method_signature", sourceMethod.getMethodSignature());
        sourceMethodJSON.put("method_name", sourceMethod.getMethodName());
        sourceMethodJSON.put("return_type", sourceMethod.getReturnType());
        sourceMethodJSON.put("arguments", serializeArguments(sourceMethod.getArguments()));
        sourceMethodJSON.put("declaring_class", sourceMethod.getDeclaringClass());

        return json;
    }

    private JSONObject serializeArguments(List<Argument> arguments) {
        JSONObject json = new JSONObject();
        for (Argument argument : arguments) {
            json.put("type", argument.getType());
            json.put("value", argument.getName());
        }
        return json;
    }
}