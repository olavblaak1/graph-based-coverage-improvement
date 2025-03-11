package com.kuleuven.MissingTestIdentification;

import com.kuleuven.Graph.Node.Node;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class MissingTestList {
    private final Map<Node, Double> missingTests;

    protected Map<Node, Double> getMissingTests() {
        return new HashMap<>(missingTests);
    }

    public MissingTestList(Map<Node, Double> missingTests) {
        this.missingTests = missingTests;
    }

    public MissingTestList(MissingTestList missingTestList) {
        this.missingTests = new HashMap<>(missingTestList.getMissingTests());
    }


    // Get a sorted JSONArray of the missing tests, sorted max to min
    public JSONArray toJSON() {
        JSONArray jsonArray = new JSONArray();
        getMissingTests().entrySet().stream()
                .sorted(Map.Entry.<Node, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("method", entry.getKey().getId());
                    jsonObject.put("importance", entry.getValue());
                    jsonArray.put(jsonObject);
                });
        return jsonArray;
    }
}
