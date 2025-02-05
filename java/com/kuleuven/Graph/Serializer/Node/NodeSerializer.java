package com.kuleuven.Graph.Serializer.Node;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kuleuven.Graph.Node.Node;

public interface NodeSerializer<T extends Node> {
    
    JSONObject serialize(T node);

    default JSONArray serialize(List<T> nodes) {
            JSONArray json = new JSONArray();
            for (T node : nodes) {
                json.put(serialize(node));
            }
            return json;
        }


    T deserialize(JSONObject json);


    default List<T> deserialize(JSONArray json) {
        List<T> nodes = new LinkedList<>();
        for (int i = 0; i < json.length(); i++) {
            nodes.add(deserialize(json.getJSONObject(i)));
        }
        return nodes;
    }
}
