package com.kuleuven.GraphExtraction.Graph.Serializer;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kuleuven.GraphExtraction.Graph.Node;

public interface NodeSerializer {
    
    public JSONObject serialize(Node node);

    public default JSONArray serialize(List<Node> nodes) {
            JSONArray json = new JSONArray();
            for (Node node : nodes) {
                json.put(serialize(node));
            }
            return json;
        }


    public Node deserialize(JSONObject json);


    public default List<Node> deserialize(JSONArray json) {
        List<Node> nodes = new LinkedList<>();
        for (int i = 0; i < json.length(); i++) {
            nodes.add(deserialize(json.getJSONObject(i)));
        }
        return nodes;
    }
}
