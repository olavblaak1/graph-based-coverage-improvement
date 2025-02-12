package com.kuleuven.Graph.Serializer.Edge;


import com.kuleuven.Graph.Edge.Edge;
import org.json.JSONObject;

public abstract class EdgeSerializer<T extends Edge> {

    public JSONObject serialize(T edge) {
        JSONObject json = new JSONObject();
        json.put("type", edge.getType());
        json.put("sourceID", edge.getSource().getId());
        json.put("destinationID", edge.getDestination().getId());
        return json;
    }

    public abstract SerializedEdge deserialize(JSONObject json);

}
