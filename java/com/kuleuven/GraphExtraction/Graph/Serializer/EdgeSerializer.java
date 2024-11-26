package com.kuleuven.GraphExtraction.Graph.Serializer;


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
}
