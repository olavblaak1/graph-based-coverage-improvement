package com.kuleuven.Graph.Serializer.Node;

import com.kuleuven.Graph.Node.MethodNode;
import com.kuleuven.Graph.Node.isOverride;
import org.json.JSONObject;

public class MethodNodeSerializer implements NodeSerializer<MethodNode> {
    @Override
    public JSONObject serialize(MethodNode node) {
        JSONObject json = new JSONObject();
        json.put("id", node.getId());
        json.put("name", node.getName());
        json.put("simpleName", node.getSimpleName());
        json.put("type", node.getType().toString());
        json.put("class", node.getClassName());
        json.put("override", node.isOverride());
        if (node.isOverride()) {
            json.put("overriddenMethod", node.getOverriddenMethodID());
        }
        json.put("signature", node.getSignature());
        return json;
    }

    @Override
    public MethodNode deserialize(JSONObject json) {
        if (json.getBoolean("override")) {
            return new MethodNode(
                    json.getString("name"),
                    isOverride.YES,
                    json.getString("signature"),
                    json.getString("overriddenMethod")
            );
        } else {
            return new MethodNode(
                    json.getString("name"),
                    json.getString("signature"),
                    isOverride.NO
            );
        }
    }
}
