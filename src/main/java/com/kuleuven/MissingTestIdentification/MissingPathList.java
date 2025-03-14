package com.kuleuven.MissingTestIdentification;

import com.kuleuven.Graph.Graph.RankedSharedPath;
import com.kuleuven.Graph.Node.Node;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MissingPathList extends MissingTestList {

    private Map<Node, Collection<RankedSharedPath>> missingPaths = new HashMap<>();

    public MissingPathList(Map<Node, Collection<RankedSharedPath>> missingPaths, Map<Node, Double> missingTests) {
        super(missingTests);
        this.missingPaths = missingPaths;
    }

    public MissingPathList(Map<Node, Collection<RankedSharedPath>> missingPaths, MissingTestList missingTests) {
        super(missingTests);
        this.missingPaths = missingPaths;
    }

    @Override
    public JSONArray toJSON() {
        JSONArray json = new JSONArray();
        getMissingTests().entrySet().stream()
                .sorted(Map.Entry.<Node, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("method", entry.getKey().getId());
                    jsonObject.put("importance", entry.getValue());

                    JSONArray paths = new JSONArray();
                    missingPaths.get(entry.getKey()).stream().
                            sorted(Comparator.comparingDouble(RankedSharedPath::getDistance))
                            .forEach(path -> {
                        JSONObject pathObject = new JSONObject();
                        pathObject.put("path", path.toJSON());
                        paths.put(pathObject);
                    });
                    jsonObject.put("paths", paths);
                    json.put(jsonObject);
                });
        return json;
    }

    @Override
    public String toString() {
        return "MissingPathList{" +
                "missingPaths=" + missingPaths +
                '}';
    }
}
