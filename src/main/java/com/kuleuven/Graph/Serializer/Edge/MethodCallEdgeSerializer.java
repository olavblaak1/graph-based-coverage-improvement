package com.kuleuven.Graph.Serializer.Edge;

import com.kuleuven.Graph.Edge.MethodCallEdge;
import org.json.JSONObject;

public class MethodCallEdgeSerializer extends EdgeSerializer<MethodCallEdge> {
    public MethodCallEdge deserialize(JSONObject json) {
        String source = json.getString("source");
        String destination = json.getString("destination");
        return new MethodCallEdge(source, destination);
    }
}