package com.kuleuven.Graph.Serializer.Edge;


import com.kuleuven.Graph.Edge.FieldEdge;
import org.json.JSONObject;

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
