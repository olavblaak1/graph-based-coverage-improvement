package com.kuleuven.Graph.Serializer.Node;

import com.kuleuven.Graph.Node.MethodNode;
import org.json.JSONObject;

import com.kuleuven.Graph.Node.isOverride;

public class MethodNodeSerializer implements NodeSerializer<MethodNode> {
    @Override
    public JSONObject serialize(MethodNode node) {
        JSONObject json = new JSONObject();
        json.put("id", node.getId());
        json.put("name", node.getName());
        json.put("type", node.getType().toString());
        json.put("class", node.getClassName());
        json.put("override", node.isOverride());
        json.put("signature", node.getSignature());
        return json;
    }

    @Override
    public MethodNode deserialize(JSONObject json) {
        return new MethodNode(
                json.getString("name"),
                isOverride.valueOf(json.getString("override")),
                json.getString("signature")
        );
    }
}
