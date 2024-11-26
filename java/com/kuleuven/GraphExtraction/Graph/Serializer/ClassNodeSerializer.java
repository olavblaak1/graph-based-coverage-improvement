package com.kuleuven.GraphExtraction.Graph.Serializer;

import org.json.JSONObject;

import com.kuleuven.GraphExtraction.Graph.Node;

public class ClassNodeSerializer implements NodeSerializer {
    @Override
    public JSONObject serialize(Node node) {
        JSONObject json = new JSONObject();
        json.put("name", node.getName());
        return json;
    }
}
