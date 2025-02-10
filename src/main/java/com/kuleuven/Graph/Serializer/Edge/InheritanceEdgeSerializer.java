package com.kuleuven.Graph.Serializer.Edge;

import com.kuleuven.Graph.Edge.InheritanceEdge;
import org.json.JSONObject;

public class InheritanceEdgeSerializer extends EdgeSerializer<InheritanceEdge> {


    public InheritanceEdge deserialize(JSONObject json) {
        String source = json.getString("source");
        String destination = json.getString("destination");
        return new InheritanceEdge(source, destination);
    }
}
