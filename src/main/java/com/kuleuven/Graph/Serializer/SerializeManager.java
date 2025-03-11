package com.kuleuven.Graph.Serializer;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Serializer.Edge.EdgeSerializerManager;
import com.kuleuven.Graph.Serializer.Edge.SerializedEdge;
import com.kuleuven.Graph.Serializer.Graph.GraphSerializerManager;
import com.kuleuven.Graph.Serializer.Graph.RankedGraphSerializer;
import com.kuleuven.Graph.Serializer.Node.NodeSerializerManager;
import org.json.JSONObject;

public class SerializeManager {
    private final EdgeSerializerManager edgeSerializerManager;
    private final NodeSerializerManager nodeSerializerManager;
    private final GraphSerializerManager graphSerializerManager;

    public SerializeManager() {
        this.edgeSerializerManager = new EdgeSerializerManager();
        this.nodeSerializerManager = new NodeSerializerManager();
        this.graphSerializerManager = new GraphSerializerManager(new RankedGraphSerializer(this));
    }

    public JSONObject serializeEdge(Edge edge) {
        return edgeSerializerManager.serializeEdge(edge);
    }

    public SerializedEdge deserializeEdge(JSONObject json) {
        return edgeSerializerManager.deserializeEdge(json);
    }

    public JSONObject serializeNode(Node node) {
        return nodeSerializerManager.serializeNode(node);
    }


    public Node deserializeNode(JSONObject json) {
        return nodeSerializerManager.deserializeNode(json);
    }

    public <T extends Graph> JSONObject serializeGraph(T graph) {
        return graphSerializerManager.serializeGraph(graph);
    }

    public <T extends Graph> T deserializeGraph(JSONObject json) {
        return graphSerializerManager.deserializeGraph(json);
    }

    public JSONObject serializeRankedGraph(RankedGraph<? extends Graph> graph) {
        return graphSerializerManager.serializeRankedGraph(graph);
    }

    public RankedGraph<? extends Graph> deserializeRankedGraph(JSONObject json) {
        return graphSerializerManager.deserializeRankedGraph(json);
    }


}