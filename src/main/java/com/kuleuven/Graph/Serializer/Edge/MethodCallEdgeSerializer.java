package com.kuleuven.Graph.Serializer.Edge;

import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Edge.MethodCallEdge;
import org.json.JSONObject;

public class MethodCallEdgeSerializer extends EdgeSerializer<MethodCallEdge> {
    public SerializedEdge deserialize(JSONObject json) {
        String sourceID = json.getString("sourceID");


        String destinationID = json.getString("destinationID");
        return new SerializedEdge(sourceID, destinationID, EdgeType.METHOD_CALL);
    }
}