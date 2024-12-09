package com.kuleuven.GraphExtraction.Graph.Serializer.Edge;


import org.json.JSONObject;

import com.kuleuven.GraphExtraction.Graph.ClassNode;
import com.kuleuven.GraphExtraction.Graph.Edge.FieldEdge;

public class FieldEdgeSerializer extends EdgeSerializer<FieldEdge> {
    public FieldEdgeSerializer() {
        super();
    }

    public JSONObject serialize(FieldEdge edge) {
        JSONObject json = new JSONObject();
        json.put("source", edge.getSource().getName());
        json.put("destination", edge.getDestination().getName());
        json.put("type", edge.getType().toString());
        return json;
    }

    public FieldEdge deserialize(JSONObject json) {
        String source = json.getString("source");
        String destination = json.getString("destination");
        return new FieldEdge(new ClassNode(source), new ClassNode(destination));
    }
}
