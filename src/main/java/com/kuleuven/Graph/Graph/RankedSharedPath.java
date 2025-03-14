package com.kuleuven.Graph.Graph;

import com.kuleuven.Graph.Node.Node;
import org.json.JSONObject;

public class RankedSharedPath extends SharedPath {
    private double distance;
    private double reciprocalDistance;

    public RankedSharedPath(Node node) {
        super(node);
        this.distance = 0.0;
        this.reciprocalDistance = 0.0;
    }


    public RankedSharedPath(Node node, double rank) {
        super(node);
        this.distance = rank;
        this.reciprocalDistance = 1/rank;
    }

    public RankedSharedPath(RankedSharedPath p) {
        super(p);
        this.distance = p.distance;
        this.reciprocalDistance = p.reciprocalDistance;
    }


    public double getDistance() {
        return distance;
    }

    public void addNode(Node node, double rank) {
        super.addNode(node);
        this.distance += rank;
        this.reciprocalDistance += 1/rank;
    }

    @Override
    public String toString() {
        return super.toString() + " distance: " + distance;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = super.toJSON();
        jsonObject.put("testingRedundancy", distance);
        return jsonObject;
    }

    public void setRank(Double distance, Double multipliedDistance, Double reciprocalDistance) {
        this.distance = distance;
        this.reciprocalDistance = reciprocalDistance;
    }

    public double getReciprocalDistance() {
        return reciprocalDistance;
    }
}
