package com.kuleuven.Graph.Serializer.Edge;


import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Edge.FieldEdge;
import com.kuleuven.Graph.Node.ClassNode;
import com.kuleuven.Graph.Node.NodeType;
import org.json.JSONObject;

public class FieldEdgeSerializer extends EdgeSerializer<FieldEdge> {
    public FieldEdgeSerializer() {
        super();
    }

    public SerializedEdge deserialize(JSONObject json) {
        String sourceID = json.getString("sourceID");
        String destinationID = json.getString("destinationID");
        return new SerializedEdge(sourceID, destinationID, EdgeType.FIELD);
    }
}
