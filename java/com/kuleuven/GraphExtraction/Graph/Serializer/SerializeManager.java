package com.kuleuven.GraphExtraction.Graph.Serializer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kuleuven.GraphExtraction.Graph.Node;
import com.kuleuven.GraphExtraction.Graph.NodeType;
import com.kuleuven.GraphExtraction.Graph.Edge.Edge;
import com.kuleuven.GraphExtraction.Graph.Edge.EdgeType;
import com.kuleuven.GraphExtraction.Graph.Serializer.Edge.EdgeSerializer;
import com.kuleuven.GraphExtraction.Graph.Serializer.Edge.FieldEdgeSerializer;
import com.kuleuven.GraphExtraction.Graph.Serializer.Edge.InheritanceEdgeSerializer;
import com.kuleuven.GraphExtraction.Graph.Serializer.Edge.MethodCallEdgeSerializer;
import com.kuleuven.GraphExtraction.Graph.Serializer.Node.ClassNodeSerializer;
import com.kuleuven.GraphExtraction.Graph.Serializer.Node.MethodNodeSerializer;
import com.kuleuven.GraphExtraction.Graph.Serializer.Node.NodeSerializer;

public class SerializeManager {
    private Map<EdgeType, EdgeSerializer<? extends Edge>> edgeSerializers = new HashMap<>();
    private Map<NodeType, NodeSerializer<? extends Node>> nodeSerializers = new HashMap<>();


    public SerializeManager() {
        edgeSerializers.put(EdgeType.METHOD_CALL, new MethodCallEdgeSerializer());
        edgeSerializers.put(EdgeType.INHERITANCE, new InheritanceEdgeSerializer());
        edgeSerializers.put(EdgeType.FIELD, new FieldEdgeSerializer());


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

    public JSONArray serializeEdges(List<Edge> edges) {
        JSONArray json = new JSONArray();
        for (Edge edge : edges) {
            json.put(serializeEdge(edge));
        }
        return json;
    }


    public Edge deserializeEdge(JSONObject json) {
        EdgeType type = EdgeType.valueOf(json.getString("type"));
        EdgeSerializer<? extends Edge> serializer = getEdgeSerializer(type);
        return serializer.deserialize(json);
    }

    public List<Edge> deserializeEdges(JSONArray json) {
        List<Edge> edges = new LinkedList<>();
        for (int i = 0; i < json.length(); i++) {
            edges.add(deserializeEdge(json.getJSONObject(i)));
        }
        return edges;
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


    public Node deserializeNode(JSONObject json) {
        NodeSerializer<? extends Node> serializer = nodeSerializers.get(NodeType.valueOf(json.getString("type")));
        return serializer.deserialize(json);
    }

    public JSONArray serializeNodes(List<Node> nodes) {
        JSONArray json = new JSONArray();
        for (Node node : nodes) {
            json.put(serializeNode(node));
        }
        return json;
    }


    public List<Node> deserializeNodes(JSONArray json) {
        List<Node> nodes = new LinkedList<>();
        for (int i = 0; i < json.length(); i++) {
            nodes.add(deserializeNode(json.getJSONObject(i)));
        }
        return nodes;
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