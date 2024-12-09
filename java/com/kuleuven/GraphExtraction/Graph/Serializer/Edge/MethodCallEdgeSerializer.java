package com.kuleuven.GraphExtraction.Graph.Serializer.Edge;

import org.json.JSONObject;

import com.kuleuven.GraphExtraction.Graph.ClassNode;
import com.kuleuven.GraphExtraction.Graph.MethodNode;
import com.kuleuven.GraphExtraction.Graph.Node;
import com.kuleuven.GraphExtraction.Graph.NodeType;
import com.kuleuven.GraphExtraction.Graph.Edge.MethodCallEdge;

public class MethodCallEdgeSerializer extends EdgeSerializer<MethodCallEdge> {

    @Override
    public JSONObject serialize(MethodCallEdge edge) {

        JSONObject json = new JSONObject();
        json.put("type", edge.getType());

        JSONObject source = new JSONObject();
        source.put("name", edge.getSource().getName());
        source.put("type", edge.getSource().getType().toString());

        JSONObject destination = new JSONObject();
        destination.put("name", edge.getDestination().getName());
        destination.put("type", edge.getDestination().getType().toString());

        json.put("source", source);
        json.put("destination", destination);
        return json;
    }

    @Override
    public MethodCallEdge deserialize(JSONObject json) {
        Node source = getNode(json.getJSONObject("source"));
        Node destination = getNode(json.getJSONObject("destination"));
        return new MethodCallEdge(source, destination);
    }


    private Node getNode(JSONObject json) {
        String name = json.getString("name");
        NodeType type = NodeType.valueOf(json.getString("type"));

        switch (type) {
            case CLASS:
                return new ClassNode(name);
            case METHOD:
                return new MethodNode(name);
            default:
                return null;
        }
    }
}