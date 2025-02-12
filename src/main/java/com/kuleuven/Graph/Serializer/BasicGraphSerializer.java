package com.kuleuven.Graph.Serializer;

import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Serializer.Edge.SerializedEdge;
import org.json.JSONArray;
import org.json.JSONObject;

public class BasicGraphSerializer implements GraphSerializer<Graph> {
    @Override
    public Graph deserializeGraph(JSONObject jsonGraph) {
        Graph graph = new CoverageGraph();

        JSONArray jsonNodes = jsonGraph.getJSONArray("nodes");
        for (int i = 0; i < jsonNodes.length(); i++) {
            JSONObject jsonNode = jsonNodes.getJSONObject(i);
            Node node = serializeManager.deserializeNode(jsonNode);
            graph.addNode(node);
        }

        JSONArray jsonEdges = jsonGraph.getJSONArray("edges");
        for (int i = 0; i < jsonEdges.length(); i++) {
            JSONObject jsonEdge = jsonEdges.getJSONObject(i);
            SerializedEdge edge = serializeManager.deserializeEdge(jsonEdge);
            graph.addEdge(getEdge(edge, graph));
        }

        return graph;
    }

    @Override
    public JSONObject serializeGraph(Graph graph) {
        JSONObject json = new JSONObject();
        json.put("graph_type", "basic");

        JSONArray jsonNodes = new JSONArray();
        for (Node node : graph.getNodes()) {
            JSONObject jsonNode = serializeManager.serializeNode(node);
            jsonNodes.put(jsonNode);
        }
        json.put("nodes", jsonNodes);

        JSONArray jsonEdges = new JSONArray();
        for (Edge edge : graph.getEdges()) {
            JSONObject jsonEdge = serializeManager.serializeEdge(edge);
            jsonEdges.put(jsonEdge);
        }
        json.put("edges", jsonEdges);

        return json;
    }
}
