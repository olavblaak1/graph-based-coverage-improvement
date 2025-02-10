package com.kuleuven.Graph.Serializer.Node;

import com.kuleuven.Graph.Node.ClassNode;
import org.json.JSONObject;

public class ClassNodeSerializer implements NodeSerializer<ClassNode> {
    @Override
    public JSONObject serialize(ClassNode node) {
        JSONObject json = new JSONObject();
        json.put("name", node.getName());
        json.put("type", node.getType().toString());
        return json;
    }

    @Override
    public ClassNode deserialize(JSONObject json) {
        return new ClassNode(json.getString("name"));
    }
}
