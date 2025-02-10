package com.kuleuven.Graph.Node;

public class RankedNode {
    double rank;
    Node node;

    public RankedNode(Node node, double rank) {
        this.rank = rank;
        this.node = node;
    }


    public double getRank() {
        return rank;
    }

    public Node getNode() {
        return node;
    }
}
