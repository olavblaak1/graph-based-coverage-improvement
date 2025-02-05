package com.kuleuven.Graph.Serializer;

import java.util.*;

import com.kuleuven.Graph.Serializer.Edge.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Node.NodeType;
import com.kuleuven.Graph.Node.RankedNode;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.EdgeType;
import com.kuleuven.Graph.Serializer.Node.ClassNodeSerializer;
import com.kuleuven.Graph.Serializer.Node.MethodNodeSerializer;
import com.kuleuven.Graph.Serializer.Node.NodeSerializer;

public class SerializeManager {
    private Map<EdgeType, EdgeSerializer<? extends Edge>> edgeSerializers = new HashMap<>();
    private Map<NodeType, NodeSerializer<? extends Node>> nodeSerializers = new HashMap<>();


    public SerializeManager() {
        edgeSerializers.put(EdgeType.METHOD_CALL, new MethodCallEdgeSerializer());
        edgeSerializers.put(EdgeType.INHERITANCE, new InheritanceEdgeSerializer());
        edgeSerializers.put(EdgeType.FIELD, new FieldEdgeSerializer());
        edgeSerializers.put(EdgeType.METHOD_OWN, new OwnsEdgeSerializer());

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

    JSONArray serializeEdges(Collection<Edge> edges) {
        JSONArray json = new JSONArray();
        for (Edge edge : edges) {
            json.put(serializeEdge(edge));
        }
        return json;
    }


    Edge deserializeEdge(JSONObject json) {
        EdgeType type = EdgeType.valueOf(json.getString("type"));
        EdgeSerializer<? extends Edge> serializer = getEdgeSerializer(type);
        return serializer.deserialize(json);
    }

    Collection<Edge> deserializeEdges(JSONArray json) {
        Set<Edge> edges = new HashSet<>();
        for (int i = 0; i < json.length(); i++) {
            edges.add(deserializeEdge(json.getJSONObject(i)));
        }
        return edges;
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

    JSONArray serializeNodes(Collection<Node> nodes) {
        JSONArray json = new JSONArray();
        for (Node node : nodes) {
            json.put(serializeNode(node));
        }
        return json;
    }


    Collection<Node> deserializeNodes(JSONArray json) {
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

    public RankedNode deserializeRankedNode(JSONObject json) {
        return new RankedNode(deserializeNode(json), json.getDouble("rank"));
    }

    public List<RankedNode> deserializeRankedNodes(JSONArray json) {
        List<RankedNode> rankedNodes = new LinkedList<>();
        for (int i = 0; i < json.length(); i++) {
            rankedNodes.add(deserializeRankedNode(json.getJSONObject(i)));
        }
        return rankedNodes;
    }


}