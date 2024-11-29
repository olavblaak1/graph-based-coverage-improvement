package com.kuleuven.GraphExtraction.Graph.Serializer;


import org.json.JSONObject;

import com.kuleuven.GraphExtraction.Graph.Node;
import com.kuleuven.GraphExtraction.Graph.NodeType;
import com.kuleuven.GraphExtraction.Graph.Edge.Edge;
import com.kuleuven.GraphExtraction.Graph.Edge.FieldEdge;

public class FieldEdgeSerializer implements EdgeSerializer{
    public FieldEdgeSerializer() {
        super();
    }

    public JSONObject serialize(Edge edge) {
        JSONObject json = new JSONObject();
        json.put("source", edge.getSource().getName());
        json.put("destination", edge.getDestination().getName());
        json.put("type", edge.getType().toString());
        return json;
    }

    public Edge deserialize(JSONObject json) {
        String source = json.getString("source");
        String destination = json.getString("destination");
        return new FieldEdge(new Node(source, NodeType.CLASS), new Node(destination, NodeType.CLASS));
    }
}
