package com.kuleuven.Graph.Serializer.Node;

import org.json.JSONObject;

import com.kuleuven.Graph.MethodNode;
import com.kuleuven.Graph.MethodNode.OverWrite;

public class MethodNodeSerializer implements NodeSerializer<MethodNode> {
    @Override
    public JSONObject serialize(MethodNode node) {
        JSONObject json = new JSONObject();
        json.put("name", node.getName());
        json.put("type", node.getType().toString());
        json.put("class", node.getClassName());
        json.put("overWrite", node.getOverWrite());
        return json;
    }

    @Override
    public MethodNode deserialize(JSONObject json) {
        return new MethodNode(json.getString("name"), OverWrite.valueOf(json.getString("overWrite")));
    }
}
