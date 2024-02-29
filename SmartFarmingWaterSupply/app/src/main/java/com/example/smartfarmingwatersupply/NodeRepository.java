package com.example.smartfarmingwatersupply;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class NodeRepository {

    List<Node> nodes = new ArrayList<>();

    public NodeRepository() {
        nodes.add(new Node(1, "925dbb80-ba15-11ee-8027-c77be3144608", "gWUf7BaZMAHPKgLR3MqG")); // physical Node
        nodes.add(new Node(2, "ac9ab050-ba21-11ee-8027-c77be3144608", "D4Hq807rOop3UecQ4YG8")); // Virtual Node
    }
    public LiveData<List<Node>> getNodes() {
        // Fetch nodes from a data source (database, API, etc.)
        // For demonstration, returning a MutableLiveData with dummy data
        MutableLiveData<List<Node>> nodesLiveData = new MutableLiveData<>();
        //List<Node> nodes = new ArrayList<>();
        // Add the nodes
        //nodes.add(new Node(1, "925dbb80-ba15-11ee-8027-c77be3144608")); // physical Node
        //nodes.add(new Node(2, "ac9ab050-ba21-11ee-8027-c77be3144608")); // Virtual Node

        nodesLiveData.setValue(nodes);
        return nodesLiveData;
    }

    public Node getNodeFromDeviceId(String deviceId) {
        for (Node node : nodes) {
            if (node.getDeviceId().equals(deviceId)) {
                return node;
            }
        }
        return null; // or throw an exception if a node must exist for a given deviceId
    }
}


