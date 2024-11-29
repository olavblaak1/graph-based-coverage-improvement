package com.kuleuven.GraphExtraction.Graph.Serializer;

import org.json.JSONObject;

import com.kuleuven.GraphExtraction.Graph.Node;
import com.kuleuven.GraphExtraction.Graph.NodeType;

public class ClassNodeSerializer implements NodeSerializer {
    @Override
    public JSONObject serialize(Node node) {
        JSONObject json = new JSONObject();
        json.put("name", node.getName());
        return json;
    }

    @Override
    public Node deserialize(JSONObject json) {
        String name = json.getString("name");
        return new Node(name, NodeType.CLASS);
    }
}
