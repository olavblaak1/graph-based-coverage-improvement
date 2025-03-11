package com.kuleuven.Graph.Serializer.Graph;

import com.kuleuven.Graph.Graph.Graph;
import com.kuleuven.Graph.Graph.RankedGraph;
import com.kuleuven.Graph.Serializer.SerializeManager;
import org.json.JSONObject;


// This can be done way better, but its fine for now.
public class RankedGraphSerializer {

    private final SerializeManager serializeManager;

    public RankedGraphSerializer(SerializeManager serializeManager) {
        this.serializeManager = serializeManager;
    }

    public <T extends Graph> RankedGraph<T> deserializeGraph(JSONObject jsonGraph) {
        T graph = serializeManager.deserializeGraph(jsonGraph);
        RankedGraph<T> rankedGraph = new RankedGraph<T>(graph);
        for (int i = 0; i < jsonGraph.getJSONArray("nodes").length(); i++) {
            JSONObject jsonNode = jsonGraph.getJSONArray("nodes").getJSONObject(i);
            rankedGraph.setRank(serializeManager.deserializeNode(jsonNode), jsonNode.getInt("rank"));
        }
        return rankedGraph;
    }

    public <T extends Graph> JSONObject serializeGraph(RankedGraph<T> graph) {
        JSONObject jsonGraph = serializeManager.serializeGraph(graph.getGraph());
        jsonGraph.put("graphType", graph.getGraph().getType());
        for (int i = 0; i < jsonGraph.getJSONArray("nodes").length(); i++) {
            JSONObject jsonNode = jsonGraph.getJSONArray("nodes").getJSONObject(i);
            jsonNode.put("rank", graph.getRank(serializeManager.deserializeNode(jsonNode)));
        }

        return jsonGraph;
    }
}
