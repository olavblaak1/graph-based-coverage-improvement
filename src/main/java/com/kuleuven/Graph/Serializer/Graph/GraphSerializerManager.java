package com.kuleuven.Graph.Serializer.Graph;

import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.GraphType;
import com.kuleuven.Graph.Graph.RankedGraph;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GraphSerializerManager {
    private final Map<GraphType, GraphSerializer<? extends Graph>> graphSerializers = new HashMap<>();
    private final RankedGraphSerializer rankedGraphSerializer;


    public GraphSerializerManager(RankedGraphSerializer rankedGraphSerializer) {
        graphSerializers.put(GraphType.BASIC, new BasicGraphSerializer());
        graphSerializers.put(GraphType.COVERAGE, new CoverageGraphSerializer());
        this.rankedGraphSerializer = rankedGraphSerializer;
    }

    public <T extends Graph> JSONObject serializeGraph(T graph) {
        GraphSerializer<T> serializer = getGraphSerializer(graph.getType());
        return serializeGraphInternal(serializer, graph);
    }


    private <T extends Graph> JSONObject serializeGraphInternal(GraphSerializer<T> serializer, Graph graph) {
        @SuppressWarnings("unchecked")
        T typedGraph = (T) graph;
        return serializer.serializeGraph(typedGraph);
    }


    private <T extends Graph> GraphSerializer<T> getGraphSerializer(GraphType type) {
        @SuppressWarnings("unchecked")
        GraphSerializer<T> serializer = (GraphSerializer<T>) graphSerializers.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for node type: " + type);
        }
        return serializer;
    }

    // Deserializes a graph, if no graphType is specified, it will default to BASIC
    public <T extends Graph> T deserializeGraph(JSONObject json) {
        GraphType type = GraphType.BASIC;
        if (json.has("graphType")) {
            type = GraphType.valueOf(json.getString("graphType"));
        }
        GraphSerializer<T> serializer = getGraphSerializer(type);
        return serializer.deserializeGraph(json);
    }

    public RankedGraph<? extends Graph> deserializeRankedGraph(JSONObject json) {
        return rankedGraphSerializer.deserializeGraph(json);
    }

    public JSONObject serializeRankedGraph(RankedGraph<? extends Graph> graph) {
        return rankedGraphSerializer.serializeGraph(graph);
    }
}
