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
import com.kuleuven.GraphExtraction.Graph.Serializer.EdgeSerializer;
import com.kuleuven.GraphExtraction.Graph.Serializer.MethodCallEdgeSerializer;

public class SerializeManager {
    private Map<EdgeType, EdgeSerializer> edgeSerializers = new HashMap<>();
    private Map<NodeType, NodeSerializer> nodeSerializers = new HashMap<>();


    public SerializeManager() {
        edgeSerializers.put(EdgeType.METHOD_CALL, new MethodCallEdgeSerializer());

        nodeSerializers.put(NodeType.CLASS, new ClassNodeSerializer());
    }

    public JSONObject serializeEdge(Edge edge) {
        EdgeSerializer serializer = edgeSerializers.get(edge.getType());
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for edge type: " + edge.getType());
        }
        return serializer.serialize(edge);
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
        EdgeSerializer serializer = edgeSerializers.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for edge type: " + type);
        }
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
        NodeSerializer serializer = nodeSerializers.get(node.getType());
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for node type: " + node.getType());
        }
        return serializer.serialize(node);
    }


    public Node deserializeNode(JSONObject json) {
        NodeType type = NodeType.valueOf(json.getString("type"));
        NodeSerializer serializer = nodeSerializers.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for node type: " + type);
        }
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
}