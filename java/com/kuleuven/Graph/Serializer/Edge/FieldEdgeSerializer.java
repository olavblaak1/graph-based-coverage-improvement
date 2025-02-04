package com.kuleuven.Graph.Serializer.Edge;


import org.json.JSONObject;

import com.kuleuven.Graph.Edge.FieldEdge;

public class FieldEdgeSerializer extends EdgeSerializer<FieldEdge> {
    public FieldEdgeSerializer() {
        super();
    }

    public FieldEdge deserialize(JSONObject json) {
        String source = json.getString("source");
        String destination = json.getString("destination");
        return new FieldEdge(source, destination);
    }
}
