package com.kuleuven.Graph.Serializer.Edge;

import com.kuleuven.Graph.Edge.EdgeType;

public class SerializedEdge {

    String sourceID;
    String destinationID;
    EdgeType edgeType;

    public SerializedEdge(String sourceID, String destinationID, EdgeType edgeType) {
        this.sourceID = sourceID;
        this.destinationID = destinationID;
        this.edgeType = edgeType;
    }

    public String getSourceID() {
        return sourceID;
    }

    public String getDestinationID() {
        return destinationID;
    }

    public EdgeType getEdgeType() {
        return edgeType;
    }

}
