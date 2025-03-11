package com.kuleuven.Graph.Graph;

import com.kuleuven.Graph.Node.Node;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/*
    * A shared path is a path that is shared between multiple paths.
    * It is used to store the common part of multiple paths for efficient storage.
    * Uses a LinkedHashSet to store the tail of the path in order, but with constant lookup, and a head to store the last node.
 */
public class SharedPath {
    private List<Node> nodes;


    public int getSize() {
        return nodes.size();
    }

    public List<Node> getNodes() {
        return new ArrayList<>(nodes);
    }

    public SharedPath(SharedPath sharedPath) {
        this.nodes = new ArrayList<>(sharedPath.getNodes());
    }

    public SharedPath(Node head) {
        this.nodes = List.of(head);
    }





    public void addNode(Node node) {
        nodes.add(node);
    }


    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Node node : nodes) {
            jsonArray.put(node.getId());
        }
        jsonObject.put("nodes", jsonArray);
        return jsonObject;
    }

    @Override
    public String toString() {
        return "SharedPath{" +
                "nodes=" + nodes +
                '}';
    }

}
