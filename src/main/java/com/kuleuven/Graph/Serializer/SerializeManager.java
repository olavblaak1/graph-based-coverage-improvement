package com.kuleuven.Graph.Serializer;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.NodeType;
import com.kuleuven.Graph.Serializer.Edge.*;
import com.kuleuven.Graph.Serializer.Node.ClassNodeSerializer;
import com.kuleuven.Graph.Serializer.Node.MethodNodeSerializer;
import com.kuleuven.Graph.Serializer.Node.NodeSerializer;
import org.json.JSONObject;

import java.util.*;

public class SerializeManager {
    private final Map<EdgeType, EdgeSerializer<? extends Edge>> edgeSerializers = new HashMap<>();
    private final Map<NodeType, NodeSerializer<? extends Node>> nodeSerializers = new HashMap<>();


    public SerializeManager() {
        edgeSerializers.put(EdgeType.METHOD_CALL, new MethodCallEdgeSerializer());
        edgeSerializers.put(EdgeType.INHERITANCE, new InheritanceEdgeSerializer());
        edgeSerializers.put(EdgeType.FIELD, new FieldEdgeSerializer());
        edgeSerializers.put(EdgeType.OWNED_BY, new OwnsEdgeSerializer());

        nodeSerializers.put(NodeType.CLASS, new ClassNodeSerializer());
        nodeSerializers.put(NodeType.METHOD, new MethodNodeSerializer());
    }

    public JSONObject serializeEdge(Edge edge) {
        EdgeSerializer<? extends Edge> serializer = getEdgeSerializer(edge.getType());
        return serializeEdgeInternal(serializer, edge);
    }

    private <T extends Edge> JSONObject serializeEdgeInternal(EdgeSerializer<T> serializer, Edge edge) {
        @SuppressWarnings("unchecked")
        T typedEdge = (T) edge;
        return serializer.serialize(typedEdge);
    }


    SerializedEdge deserializeEdge(JSONObject json) {
        EdgeType type = EdgeType.valueOf(json.getString("type"));
        EdgeSerializer<? extends Edge> serializer = getEdgeSerializer(type);
        return serializer.deserialize(json);
    }

    JSONObject serializeNode(Node node) {
        NodeSerializer<? extends Node> serializer = getNodeSerializer(node.getType());
        return serializeNodeInternal(serializer, node);
    }

    private <T extends Node> JSONObject serializeNodeInternal(NodeSerializer<T> serializer, Node node) {
        @SuppressWarnings("unchecked")
        T typedNode = (T) node;
        return serializer.serialize(typedNode);
    }


    Node deserializeNode(JSONObject json) {
        NodeSerializer<? extends Node> serializer = nodeSerializers.get(NodeType.valueOf(json.getString("type")));
        return serializer.deserialize(json);
    }


    private <T extends Edge> EdgeSerializer<T> getEdgeSerializer(EdgeType type) {
        @SuppressWarnings("unchecked")
        EdgeSerializer<T> serializer = (EdgeSerializer<T>) edgeSerializers.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for edge type: " + type);
        }
        return serializer;
    }


    private <T extends Node> NodeSerializer<T> getNodeSerializer(NodeType type) {
        @SuppressWarnings("unchecked")
        NodeSerializer<T> serializer = (NodeSerializer<T>) nodeSerializers.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for node type: " + type);
        }
        return serializer;
    }


}