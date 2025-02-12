package com.kuleuven.Graph.Serializer.Edge;

import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Edge.OwnedByEdge;
import com.kuleuven.Graph.Node.ClassNode;
import org.json.JSONObject;

public class OwnsEdgeSerializer extends EdgeSerializer<OwnedByEdge> {
    public SerializedEdge deserialize(JSONObject json) {
        String sourceID = json.getString("owner");
        String destinationID = json.getString("method");
        return new SerializedEdge(sourceID, destinationID, EdgeType.OWNED_BY);
    }
}
