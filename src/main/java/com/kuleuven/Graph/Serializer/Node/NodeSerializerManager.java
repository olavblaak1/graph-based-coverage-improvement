package com.kuleuven.Graph.Serializer.Node;

import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.NodeType;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NodeSerializerManager {

    private final Map<NodeType, NodeSerializer<? extends Node>> nodeSerializers = new HashMap<>();

    public NodeSerializerManager() {
        nodeSerializers.put(NodeType.CLASS, new ClassNodeSerializer());
        nodeSerializers.put(NodeType.METHOD, new MethodNodeSerializer());
    }

    public Node deserializeNode(JSONObject json) {
        NodeSerializer<? extends Node> serializer = nodeSerializers.get(NodeType.valueOf(json.getString("type")));
        return serializer.deserialize(json);
    }

    private <T extends Node> NodeSerializer<T> getNodeSerializer(NodeType type) {
        @SuppressWarnings("unchecked")
        NodeSerializer<T> serializer = (NodeSerializer<T>) nodeSerializers.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for node type: " + type);
        }
        return serializer;
    }

    public JSONObject serializeNode(Node node) {
        NodeSerializer<? extends Node> serializer = getNodeSerializer(node.getType());
        return serializeNodeInternal(serializer, node);
    }

    private <T extends Node> JSONObject serializeNodeInternal(NodeSerializer<T> serializer, Node node) {
        @SuppressWarnings("unchecked")
        T typedNode = (T) node;
        return serializer.serialize(typedNode);
    }
}
