package com.kuleuven.Graph.Serializer.Edge;

import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Edge.EdgeType;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EdgeSerializerManager {

    private final Map<EdgeType, EdgeSerializer<? extends Edge>> edgeSerializers = new HashMap<>();

    public EdgeSerializerManager() {
        edgeSerializers.put(EdgeType.METHOD_CALL, new MethodCallEdgeSerializer());
        edgeSerializers.put(EdgeType.INHERITANCE, new InheritanceEdgeSerializer());
        edgeSerializers.put(EdgeType.FIELD, new FieldEdgeSerializer());
        edgeSerializers.put(EdgeType.OWNED_BY, new OwnsEdgeSerializer());
        edgeSerializers.put(EdgeType.OVERRIDES, new OverridesEdgeSerializer());
        edgeSerializers.put(EdgeType.FIELD_ACCESS, new FieldAccessEdgeSerializer());
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


    public SerializedEdge deserializeEdge(JSONObject json) {
        EdgeType type = EdgeType.valueOf(json.getString("edgeType"));
        EdgeSerializer<? extends Edge> serializer = getEdgeSerializer(type);
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
}
