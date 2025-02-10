package com.kuleuven.Graph.Serializer;

import com.kuleuven.Graph.Graph;
import org.json.JSONObject;

public interface GraphSerializer<T extends Graph> {

    SerializeManager serializeManager = new SerializeManager();

    T deserializeGraph(JSONObject jsonGraph);

    JSONObject serializeGraph(T graph);
}
