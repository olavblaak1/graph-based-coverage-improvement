package com.kuleuven.Graph.Graph;

import com.kuleuven.Graph.Node.Node;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/*
    * A shared path is a path that is shared between multiple paths.
    * It is used to store the common part of multiple paths for efficient storage.
    * Uses a LinkedHashSet to store the tail of the path in order, but with constant lookup, and a head to store the last node.
 */
public class SharedPath {
    private Node head;
    private SharedPath tail;



    public SharedPath(SharedPath sharedPath) {
        if (!sharedPath.getLastNode().isPresent()) {
            this.head = null;
            this.tail = null;
            return;
        }
        if (!sharedPath.getTail().isPresent()) {
            this.head = sharedPath.getLastNode().get();
            this.tail = null;
            return;
        }

        this.head = sharedPath.head;
        this.tail = sharedPath.tail;
    }

    public SharedPath(Node head) {
        this.head = head;
        this.tail = null;
    }

    public SharedPath(Node head, SharedPath tail) {
        this.head = head;
        this.tail = tail;
    }

    public Optional<Node> getLastNode() {
        return Optional.ofNullable(head);
    }

    public Optional<SharedPath> getTail() {
        return Optional.ofNullable(tail);
    }



    public void addNode(Node node) {
        if (head == null) {
            this.head = node;
            return;
        }

        if (tail == null) {
            this.tail = new SharedPath(head);
        }
        this.tail = this;
        this.head = node;
    }


    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<Node> nodes = new ArrayList<>();
        Optional<SharedPath> current = Optional.of(this);
        while (current.isPresent()) {
            current = current.get().getTail();
            current.ifPresent(sharedPath -> nodes.add(sharedPath.head));
        }

        for (Node node : nodes.reversed()) {
            jsonArray.put(node.getId());
        }

        json.put("nodes", jsonArray);

        return json;
    }

}
