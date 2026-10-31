package com.example;

import java.util.*;

public class ShortPath {

    // Helper class for the Priority Queue (Your Pair class, adapted for Strings)
    public static class Pair implements Comparable<Pair> {
        String location;
        int dist;

        public Pair(String location, int dist) {
            this.location = location;
            this.dist = dist;
        }

        @Override
        public int compareTo(Pair p2) {
            return this.dist - p2.dist; // Sort by shortest distance
        }
    }

    // Result class to hold both the distance and the actual path
    public static class PathResult {
        int totalDistance;
        List<String> path;

        public PathResult(int totalDistance, List<String> path) {
            this.totalDistance = totalDistance;
            this.path = path;
        }
    }

    // The Dijkstra Algorithm
    public static PathResult findShortestPath(CityGraph city, String start, String destination) {
        Map<String, List<CityGraph.Edge>> graph = city.getGraph();
        
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>(); // To reconstruct the path
        Set<String> visited = new HashSet<>();

        // Initialize distances to infinity
        for (String loc : graph.keySet()) {
            dist.put(loc, Integer.MAX_VALUE);
            parent.put(loc, null);
        }

        dist.put(start, 0);
        pq.add(new Pair(start, 0));

        while (!pq.isEmpty()) {
            Pair curr = pq.remove();
            String u = curr.location;

            if (visited.contains(u)) continue;
            visited.add(u);

            // If we reached the destination, we can stop early (optional optimization)
            if (u.equals(destination)) break;

            for (CityGraph.Edge edge : graph.getOrDefault(u, Collections.emptyList())) {
                String v = edge.destination;
                int weight = edge.weight;

                if (dist.get(u) + weight < dist.get(v)) {
                    dist.put(v, dist.get(u) + weight);
                    parent.put(v, u); // Record where we came from
                    pq.add(new Pair(v, dist.get(v)));
                }
            }
        }

        // Reconstruct the path from destination back to start
        List<String> path = new ArrayList<>();
        String current = destination;
        
        // If destination was never reached
        if (dist.get(destination) == Integer.MAX_VALUE) {
            return new PathResult(-1, path); 
        }

        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }
        Collections.reverse(path); // Reverse to get Start -> Destination

        return new PathResult(dist.get(destination), path);
    }
}