package com.kuleuven.Graph.Serializer.Graph;

import com.kuleuven.Graph.Graph.CoverageGraph;
import com.kuleuven.Graph.Edge.Edge;
import com.kuleuven.Graph.Node.Node;
import com.kuleuven.Graph.Serializer.Edge.SerializedEdge;
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
            graph.addNode(node);
            if (jsonNode.getBoolean("covered")) {
                graph.setMarkCount(node, jsonNode.getInt("coverageCount"));
            }
        }

        JSONArray jsonEdges = jsonGraph.getJSONArray("edges");
        for (int i = 0; i < jsonEdges.length(); i++) {
            JSONObject jsonEdge = jsonEdges.getJSONObject(i);
            SerializedEdge serializedEdge = serializeManager.deserializeEdge(jsonEdge);
            System.out.println("Edge : " + serializedEdge.getSourceID() + " -> " + serializedEdge.getDestinationID());
            Edge edge = getEdge(serializedEdge, graph);
            graph.addEdge(edge);
            if (jsonEdge.getBoolean("covered")) {
                graph.setMarkCount(edge, jsonEdge.getInt("coverageCount"));
            }
        }

        return graph;
    }

    @Override
    public JSONObject serializeGraph(CoverageGraph graph) {
        JSONObject json = new JSONObject();
        json.put("graphType", graph.getType());

        JSONArray jsonNodes = new JSONArray();
        for (Node node : graph.getNodes()) {
            JSONObject jsonNode = serializeManager.serializeNode(node);
            jsonNode.put("covered", graph.isNodeMarked(node));
            jsonNode.put("coverageCount", graph.getMarkedNodeCount(node));
            jsonNodes.put(jsonNode);
        }
        json.put("nodes", jsonNodes);

        JSONArray jsonEdges = new JSONArray();
        for (Edge edge : graph.getEdges()) {
            JSONObject jsonEdge = serializeManager.serializeEdge(edge);
            jsonEdge.put("covered", graph.isEdgeMarked(edge));
            jsonEdge.put("coverageCount", graph.getMarkedEdgeCount(edge));
            jsonEdges.put(jsonEdge);
        }
        json.put("edges", jsonEdges);

        return json;
    }
}