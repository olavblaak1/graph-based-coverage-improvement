package com.kuleuven.GraphExtraction.Graph.Serializer;


import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kuleuven.GraphExtraction.Graph.Edge.Edge;

public interface EdgeSerializer {
    
    public JSONObject serialize(Edge edge);

    public default JSONArray serialize(List<Edge> edges) {
            JSONArray json = new JSONArray();
            for (Edge edge : edges) {
                json.put(serialize(edge));
            }
            return json;
        }

    public Edge deserialize(JSONObject json);


    public default List<Edge> deserialize(JSONArray json) {
        List<Edge> edges = new LinkedList<>();
        for (int i = 0; i < json.length(); i++) {
            edges.add(deserialize(json.getJSONObject(i)));
        }
        return edges;
    }
}
