package com.kuleuven.Graph.Serializer.Edge;


import org.json.JSONObject;

import com.kuleuven.Graph.Edge.Edge;
public abstract class EdgeSerializer <T extends Edge> {
    
    public JSONObject serialize(T edge) {
        JSONObject json = new JSONObject();
        json.put("type", edge.getType());
        json.put("source", edge.getSource());
        json.put("destination", edge.getDestination());
        return json;
    }

    public abstract T deserialize(JSONObject json);

}
