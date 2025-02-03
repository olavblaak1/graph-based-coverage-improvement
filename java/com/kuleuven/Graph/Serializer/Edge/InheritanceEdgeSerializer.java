package com.kuleuven.Graph.Serializer.Edge;

import org.json.JSONObject;

import com.kuleuven.Graph.ClassNode;
import com.kuleuven.Graph.Edge.InheritanceEdge;

public class InheritanceEdgeSerializer extends EdgeSerializer<InheritanceEdge> {
    

    public JSONObject serialize(InheritanceEdge edge) {
        JSONObject json = new JSONObject();
        json.put("source", edge.getSource().getName());
        json.put("destination", edge.getDestination().getName());
        json.put("type", edge.getType().toString());
        return json;
    }

    public InheritanceEdge deserialize(JSONObject json) {
        String source = json.getString("source");
        String destination = json.getString("destination");
        return new InheritanceEdge(new ClassNode(source), new ClassNode(destination));
    }
}
