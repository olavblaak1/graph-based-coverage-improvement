package com.kuleuven.Graph.Serializer.Edge;

import org.json.JSONObject;

import com.kuleuven.Graph.Edge.MethodCallEdge;

public class MethodCallEdgeSerializer extends EdgeSerializer<MethodCallEdge> {
    public MethodCallEdge deserialize(JSONObject json) {
        String source = json.getString("source");
        String destination = json.getString("destination");
        return new MethodCallEdge(source, destination);
    }
}