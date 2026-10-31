package com.example;

import java.util.*;

public class CityGraph {

    public static class Edge {
        String destination;
        int weight;

        public Edge(String destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    private Map<String, List<Edge>> adjList = new HashMap<>();

    public void addLocation(String name) {
        adjList.putIfAbsent(name, new ArrayList<>());
    }

    public void addRoad(String from, String to, int distance) {
        adjList.get(from).add(new Edge(to, distance));
        adjList.get(to).add(new Edge(from, distance));
    }

    public void buildCity() {
        String[] locations = {
            "johnsHouse", "sarahsHouse", "hospital", "school", 
            "park", "office", "mall", "fireStation", "police", "jimsHouse",
            "abandonedHouse", "clairsHouse", "charliesHouse", "residentialBuilding"
        };
        
        for (String loc : locations) addLocation(loc);

        // REMOVED: johnsHouse-hospital (X marked)
        // REMOVED: mall-fireStation (X marked)
        // REMOVED: mall-police (X marked)
        // REMOVED: police-fireStation (X marked)
        // REMOVED: office-fireStation (X marked)

        // ACTIVE ROADS (Matching FXML)
        addRoad("johnsHouse", "sarahsHouse", 2);
        addRoad("johnsHouse", "school", 6);
        
        addRoad("sarahsHouse", "office", 2);
        addRoad("sarahsHouse", "park", 8);
        
        addRoad("hospital", "school", 4);
        addRoad("hospital", "park", 8);
        
        addRoad("school", "mall", 3);
        
        addRoad("park", "office", 2);
        addRoad("park", "mall", 5);      // NEW BLACK PATH
        addRoad("park", "police", 6);
        
        addRoad("mall", "police", 7);
        
        addRoad("police", "jimsHouse", 5);  // NEW BLACK PATH
        
        addRoad("office", "fireStation", 9);
        
        addRoad("fireStation", "police", 6);

        // Clair's House (only connected to Mall)
        addRoad("clairsHouse", "mall", 4);

        // Charlie's House (only connected to Office)
        addRoad("charliesHouse", "office", 3);

        // Residential Building (connected to Mall, School, and Office)
        addRoad("residentialBuilding", "mall", 4);
        addRoad("residentialBuilding", "school", 3);
        addRoad("residentialBuilding", "office", 5);

        // Note: abandonedHouse is an isolated node with no connections (no shortest path)
    }

    public Map<String, List<Edge>> getGraph() {
        return adjList;
    }
}
