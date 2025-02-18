package com.kuleuven.Graph.Serializer;

import com.kuleuven.Graph.Edge.*;
import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Serializer.Edge.SerializedEdge;
import org.json.JSONObject;

import java.util.Optional;

public interface GraphSerializer<T extends Graph> {

    SerializeManager serializeManager = new SerializeManager();

    T deserializeGraph(JSONObject jsonGraph);

    JSONObject serializeGraph(T graph);

    default Edge getEdge(SerializedEdge edge, Graph graph) {
        Optional<Node> source = graph.getNode(edge.getSourceID());
        Optional<Node> destination = graph.getNode(edge.getDestinationID());

        if (!source.isPresent() || !destination.isPresent()) {
            throw new IllegalArgumentException("Edge does not have corresponding edges");
        }

        Node sourceNode = source.get();
        Node destinationNode = destination.get();

        switch (edge.getEdgeType()) {
            case INHERITANCE:
                return new InheritanceEdge(sourceNode, destinationNode);
            case FIELD:
                return new FieldEdge(sourceNode, destinationNode);
            case OWNED_BY:
                return new OwnedByEdge(sourceNode, destinationNode);
            case METHOD_CALL:
                return new MethodCallEdge(sourceNode, destinationNode);
            case OVERRIDES:
                return new OverridesEdge(sourceNode, destinationNode);
            case FIELD_ACCESS:
                return new FieldAccessEdge(sourceNode, destinationNode);
            default:
                throw new IllegalArgumentException("EdgeType " + edge.getEdgeType() +  " serialization not supported");
        }
    }
}
