package com.kuleuven.Graph.Serializer;

import com.kuleuven.Graph.CoverageGraph;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Graph;
import com.kuleuven.Graph.Node.Node;
import org.json.JSONArray;
import org.json.JSONObject;

public class CoverageGraphSerializer implements GraphSerializer<CoverageGraph> {

    @Override
    public CoverageGraph deserializeGraph(JSONObject jsonGraph) {
        CoverageGraph graph = new CoverageGraph();

        JSONArray jsonNodes = jsonGraph.getJSONArray("nodes");
        for (int i = 0; i < jsonNodes.length(); i++) {
            JSONObject jsonNode = jsonNodes.getJSONObject(i);
            Node node = serializeManager.deserializeNode(jsonNode);
            if (jsonNode.getBoolean("covered")) {
                graph.markNode(node);
            }
        }

        JSONArray jsonEdges = jsonGraph.getJSONArray("edges");
        for (int i = 0; i < jsonEdges.length(); i++) {
            JSONObject jsonEdge = jsonEdges.getJSONObject(i);
            Edge edge = serializeManager.deserializeEdge(jsonEdge);
            graph.addEdge(edge);
            if (jsonEdge.getBoolean("covered")) {
                graph.markEdge(edge);
            }
        }

        return graph;
    }

    @Override
    public JSONObject serializeGraph(CoverageGraph graph) {
        JSONObject json = new JSONObject();
        json.put("graph_type", "coverage");

        JSONArray jsonNodes = new JSONArray();
        for (Node node : graph.getNodes()) {
            JSONObject jsonNode = serializeManager.serializeNode(node);
            jsonNode.put("covered", graph.isNodeMarked(node));
            jsonNodes.put(jsonNode);
        }
        json.put("nodes", jsonNodes);

        JSONArray jsonEdges = new JSONArray();
        for (Edge edge : graph.getEdges()) {
            JSONObject jsonEdge = serializeManager.serializeEdge(edge);
            jsonEdge.put("covered", graph.isEdgeMarked(edge));
            jsonEdges.put(jsonEdge);
        }
        json.put("edges", jsonEdges);

        return json;
    }
}