package com.kuleuven.Graph.Serializer.Edge;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Edge.FieldAccessEdge;
import org.json.JSONObject;

public class FieldAccessEdgeSerializer extends EdgeSerializer<FieldAccessEdge> {
    @Override
    public SerializedEdge deserialize(JSONObject json) {
        String sourceID = json.getString("sourceID");
        String destinationID = json.getString("destinationID");
        return new SerializedEdge(sourceID, destinationID, EdgeType.FIELD_ACCESS);
    }
}
