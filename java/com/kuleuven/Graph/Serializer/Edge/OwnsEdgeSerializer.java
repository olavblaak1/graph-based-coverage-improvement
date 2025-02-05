package com.kuleuven.Graph.Serializer.Edge;

import com.kuleuven.Graph.Edge.OwnsMethodEdge;
import org.json.JSONObject;

public class OwnsEdgeSerializer extends EdgeSerializer<OwnsMethodEdge> {
    public OwnsMethodEdge deserialize(JSONObject json) {
        String source = json.getString("owner");
        String destination = json.getString("method");
        return new OwnsMethodEdge(source, destination);
    }
}
