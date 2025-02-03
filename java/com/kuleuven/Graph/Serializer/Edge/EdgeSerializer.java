package com.kuleuven.Graph.Serializer.Edge;


import org.json.JSONObject;

import com.kuleuven.Graph.Edge.Edge;
public abstract class EdgeSerializer <T extends Edge> {
    
    public abstract JSONObject serialize(T edge);

    public abstract T deserialize(JSONObject json);

}
