package com.kuleuven.GraphExtraction.Graph.Serializer;

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
}
