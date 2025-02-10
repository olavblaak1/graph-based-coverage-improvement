package com.kuleuven.Graph.Serializer.Edge;

import com.kuleuven.Graph.Edge.OwnedByEdge;
import org.json.JSONObject;

public class OwnsEdgeSerializer extends EdgeSerializer<OwnedByEdge> {
    public OwnedByEdge deserialize(JSONObject json) {
        String source = json.getString("owner");
        String destination = json.getString("method");
        return new OwnedByEdge(source, destination);
    }
}
