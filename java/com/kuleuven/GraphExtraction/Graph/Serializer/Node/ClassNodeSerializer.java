package com.kuleuven.GraphExtraction.Graph.Serializer.Node;

import org.json.JSONObject;

import com.kuleuven.GraphExtraction.Graph.ClassNode;
import com.kuleuven.GraphExtraction.Graph.MethodNode;
import com.kuleuven.GraphExtraction.Graph.Node;
import com.kuleuven.GraphExtraction.Graph.NodeType;

public class ClassNodeSerializer implements NodeSerializer {
    @Override
    public JSONObject serialize(Node node) {
        JSONObject json = new JSONObject();
        json.put("name", node.getName());
        json.put("type", node.getType().toString());
        return json;
    }

    @Override
    public Node deserialize(JSONObject json) {
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
