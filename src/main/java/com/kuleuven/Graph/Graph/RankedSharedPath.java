package com.kuleuven.Graph.Graph;

import com.kuleuven.Graph.Node.Node;
import org.json.JSONObject;

public class RankedSharedPath extends SharedPath {
    private double rank;

    public RankedSharedPath(Node node) {
        super(node);
        this.rank = 0.0;
    }

    public RankedSharedPath(RankedSharedPath p) {
        super(p);
        this.rank = p.rank;
    }


    public double getRank() {
        return rank;
    }

    public void addNode(Node node, double rank) {
        super.addNode(node);
        this.rank += rank;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = super.toJSON();
        jsonObject.put("rank", rank);
        return jsonObject;
    }

}
