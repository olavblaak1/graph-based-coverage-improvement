package com.kuleuven.Graph.Serializer.Edge;

import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Edge.OverridesEdge;
import org.json.JSONObject;

public class OverridesEdgeSerializer extends EdgeSerializer<OverridesEdge> {

    @Override
    public SerializedEdge deserialize(JSONObject json) {

        String sourceID = json.getString("sourceID");

        String destinationID = json.getString("destinationID");
        return new SerializedEdge(sourceID, destinationID, EdgeType.OVERRIDES);
    }
}
