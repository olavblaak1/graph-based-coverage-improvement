package com.kuleuven.Graph.Serializer.Node;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kuleuven.Graph.Node;

public interface NodeSerializer<T extends Node> {
    
    public JSONObject serialize(T node);

    public default JSONArray serialize(List<T> nodes) {
            JSONArray json = new JSONArray();
            for (T node : nodes) {
                json.put(serialize(node));
            }
            return json;
        }


    public T deserialize(JSONObject json);


    public default List<T> deserialize(JSONArray json) {
        List<T> nodes = new LinkedList<>();
        for (int i = 0; i < json.length(); i++) {
            nodes.add(deserialize(json.getJSONObject(i)));
        }
        return nodes;
    }
}
