package com.kuleuven.GraphExtraction.Graph.Serializer;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kuleuven.GraphExtraction.Graph.ClassNode;
import com.kuleuven.GraphExtraction.Graph.Node;
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
        json.put("type", edge.getType());
        json.put("source", methodCallEdge.getSource().getName());
        json.put("destination", methodCallEdge.getDestination().getName());

        Method linkMethod = methodCallEdge.getLinkMethod();
        Method sourceMethod = methodCallEdge.getSourceMethod();

        JSONObject linkMethodJSON = new JSONObject();
        linkMethodJSON.put("method_signature", linkMethod.getMethodSignature());
        linkMethodJSON.put("method_name", linkMethod.getMethodName());
        linkMethodJSON.put("return_type", linkMethod.getReturnType());
        linkMethodJSON.put("arguments", serializeArguments(linkMethod.getParameters()));
        linkMethodJSON.put("declaring_class", linkMethod.getDeclaringClass());
        
        JSONObject sourceMethodJSON = new JSONObject();
        sourceMethodJSON.put("method_signature", sourceMethod.getMethodSignature());
        sourceMethodJSON.put("method_name", sourceMethod.getMethodName());
        sourceMethodJSON.put("return_type", sourceMethod.getReturnType());
        sourceMethodJSON.put("arguments", serializeArguments(sourceMethod.getParameters()));
        sourceMethodJSON.put("declaring_class", sourceMethod.getDeclaringClass());

        json.put("link_method", linkMethodJSON);
        json.put("source_method", sourceMethodJSON);

        return json;
    }

    private JSONArray serializeArguments(List<String> arguments) {

        JSONArray json = new JSONArray();

        arguments.forEach(
            argument -> {
                JSONObject argumentJSON = new JSONObject();
                argumentJSON.put("type", argument);
                json.put(argumentJSON);
            }
        );
        
        return json;
    }

    @Override
    public Edge deserialize(JSONObject json) {
        String source = json.getString("source");
        Node sourceNode = new ClassNode(source);


        String destination = json.getString("destination");
        Node destinationNode = new ClassNode(destination);

        JSONObject linkMethodJSON = json.getJSONObject("link_method");
        String linkMethodSignature = linkMethodJSON.getString("method_signature");
        String linkMethodName = linkMethodJSON.getString("method_name");
        String linkMethodReturnType = linkMethodJSON.getString("return_type");
        String linkMethodDeclaringClass = linkMethodJSON.getString("declaring_class");
        List<String> linkMethodArguments = deserializeArguments(linkMethodJSON.getJSONArray("arguments"));
        Method linkMethod = new Method(linkMethodName, linkMethodReturnType, linkMethodDeclaringClass, linkMethodSignature, linkMethodArguments);

        JSONObject sourceMethodJSON = json.getJSONObject("source_method");
        String sourceMethodSignature = sourceMethodJSON.getString("method_signature");
        String sourceMethodName = sourceMethodJSON.getString("method_name");
        String sourceMethodReturnType = sourceMethodJSON.getString("return_type");
        String sourceMethodDeclaringClass = sourceMethodJSON.getString("declaring_class");
        List<String> sourceMethodArguments = deserializeArguments(sourceMethodJSON.getJSONArray("arguments"));
        Method sourceMethod = new Method(sourceMethodName, sourceMethodReturnType, sourceMethodDeclaringClass, sourceMethodSignature, sourceMethodArguments);

        return new MethodCallEdge(sourceNode, destinationNode, linkMethod, sourceMethod);
    }

    private List<String> deserializeArguments(JSONArray json) {
        List<String> arguments = new LinkedList<>();
        for (int i = 0; i < json.length(); i++) {
            JSONObject argumentJSON = json.getJSONObject(i);
            arguments.add(argumentJSON.getString("type"));
        }
        return arguments;
    }
}