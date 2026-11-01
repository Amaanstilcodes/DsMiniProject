package com.example;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class ShortPathTest {

    private CityGraph city;
    private static final String[] ALL_LOCATIONS = {
        "johnsHouse", "sarahsHouse", "hospital", "school", 
        "park", "office", "mall", "fireStation", "police", "jimsHouse",
        "abandonedHouse", "clairsHouse", "charliesHouse", "residentialBuilding"
    };

    @Before
    public void setUp() {
        city = new CityGraph();
        city.buildCity();
    }

    /**
     * Helper to compute the total distance along a reconstructed path
     * using the actual edge weights in the graph.
     */
    private int computePathDistance(List<String> path) {
        if (path == null || path.isEmpty()) return -1;
        if (path.size() == 1) return 0;

        Map<String, List<CityGraph.Edge>> g = city.getGraph();
        int sum = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            String u = path.get(i);
            String v = path.get(i + 1);

            boolean found = false;
            for (CityGraph.Edge edge : g.getOrDefault(u, Collections.emptyList())) {
                if (edge.destination.equals(v)) {
                    sum += edge.weight;
                    found = true;
                    break;
                }
            }
            assertTrue("Consecutive nodes in path must have a valid connecting road: " + u + " -> " + v, found);
        }
        return sum;
    }

    /**
     * Helper: Independent Floyd-Warshall implementation to verify Dijkstra distances.
     */
    private Map<String, Map<String, Integer>> computeFloydWarshall() {
        Map<String, Map<String, Integer>> dist = new HashMap<>();
        int INF = 1_000_000_000;

        for (String u : ALL_LOCATIONS) {
            dist.put(u, new HashMap<>());
            for (String v : ALL_LOCATIONS) {
                dist.get(u).put(v, u.equals(v) ? 0 : INF);
            }
        }

        Map<String, List<CityGraph.Edge>> g = city.getGraph();
        for (String u : g.keySet()) {
            for (CityGraph.Edge edge : g.get(u)) {
                dist.get(u).put(edge.destination, Math.min(dist.get(u).get(edge.destination), edge.weight));
            }
        }

        for (String k : ALL_LOCATIONS) {
            for (String i : ALL_LOCATIONS) {
                for (String j : ALL_LOCATIONS) {
                    if (dist.get(i).get(k) + dist.get(k).get(j) < dist.get(i).get(j)) {
                        dist.get(i).put(j, dist.get(i).get(k) + dist.get(k).get(j));
                    }
                }
            }
        }
        return dist;
    }

    // ==========================================
    // 1. VERIFY PRESET TEST CASES (from UI)
    // ==========================================

    @Test
    public void testPreset1_JohnToJim() {
        // Longest route traversing across the entire map
        ShortPath.PathResult res = ShortPath.findShortestPath(city, "johnsHouse", "jimsHouse");
        assertEquals("Distance for John -> Jim must be 17 km", 17, res.totalDistance);
        assertEquals("Path must start at John's House", "johnsHouse", res.path.get(0));
        assertEquals("Path must end at Jim's House", "jimsHouse", res.path.get(res.path.size() - 1));
        assertEquals("Sum of road segments must equal total distance", 17, computePathDistance(res.path));

        // Expected optimal path: John -> Sarah (2) -> Office (2) -> Park (2) -> Police (6) -> Jim (5) = 17
        List<String> expectedPath = Arrays.asList("johnsHouse", "sarahsHouse", "office", "park", "police", "jimsHouse");
        assertEquals(expectedPath, res.path);
    }

    @Test
    public void testPreset2_HospitalToPolice() {
        // Civic service route
        ShortPath.PathResult res = ShortPath.findShortestPath(city, "hospital", "police");
        assertEquals("Distance for Hospital -> Police must be 14 km", 14, res.totalDistance);
        assertEquals("Sum of road segments must equal total distance", 14, computePathDistance(res.path));

        // Expected optimal path: Hospital -> School (4) -> Mall (3) -> Police (7) = 14 km
        // Alternate path: Hospital -> Park (8) -> Police (6) = 14 km (equal distance)
        assertTrue(res.totalDistance == 14);
    }

    @Test
    public void testPreset3_SarahToMall() {
        // Shopping transit route
        ShortPath.PathResult res = ShortPath.findShortestPath(city, "sarahsHouse", "mall");
        assertEquals("Distance for Sarah -> Mall must be 9 km", 9, res.totalDistance);
        assertEquals("Sum of road segments must equal total distance", 9, computePathDistance(res.path));

        // Optimal path: Sarah -> Office (2) -> Park (2) -> Mall (5) = 9 km
        List<String> expectedPath = Arrays.asList("sarahsHouse", "office", "park", "mall");
        assertEquals(expectedPath, res.path);
    }

    @Test
    public void testPreset4_ClairToCharlie() {
        // Cross-city route through newly added house nodes
        ShortPath.PathResult res = ShortPath.findShortestPath(city, "clairsHouse", "charliesHouse");
        assertEquals("Distance for Clair -> Charlie must be 14 km", 14, res.totalDistance);
        assertEquals("Sum of road segments must equal total distance", 14, computePathDistance(res.path));

        // Clair -> Mall (4) -> Park (5) -> Office (2) -> Charlie (3) = 14 km
        // (Alternate via Residential: Clair -> Mall (4) -> Residential (4) -> Office (5) -> Charlie (3) = 16 km, which is longer)
        // Dijkstra correctly picked 14 km!
        List<String> expectedPath = Arrays.asList("clairsHouse", "mall", "park", "office", "charliesHouse");
        assertEquals(expectedPath, res.path);
    }

    @Test
    public void testPreset5_ResidentialToJim() {
        // Outbound route from Residential Building
        ShortPath.PathResult res = ShortPath.findShortestPath(city, "residentialBuilding", "jimsHouse");
        assertEquals("Distance for Residential -> Jim must be 16 km", 16, res.totalDistance);
        assertEquals("Sum of road segments must equal total distance", 16, computePathDistance(res.path));

        // Residential -> Mall (4) -> Police (7) -> Jim (5) = 16 km
        List<String> expectedPath = Arrays.asList("residentialBuilding", "mall", "police", "jimsHouse");
        assertEquals(expectedPath, res.path);
    }

    @Test
    public void testPreset6_AbandonedHouseUnreachable() {
        // Isolated node: No shortest path exists
        ShortPath.PathResult resOut = ShortPath.findShortestPath(city, "abandonedHouse", "mall");
        assertEquals("Outbound from Abandoned House must return -1", -1, resOut.totalDistance);
        assertTrue("Path must be empty when no route exists", resOut.path.isEmpty());

        ShortPath.PathResult resIn = ShortPath.findShortestPath(city, "johnsHouse", "abandonedHouse");
        assertEquals("Inbound to Abandoned House must return -1", -1, resIn.totalDistance);
        assertTrue("Path must be empty when no route exists", resIn.path.isEmpty());
    }

    // ==========================================
    // 2. EXHAUSTIVE ALL-PAIRS VERIFICATION (196 PAIRS)
    // ==========================================

    @Test
    public void testAll196PairsMatchFloydWarshallAndAreStrictlyMinimal() {
        Map<String, Map<String, Integer>> fw = computeFloydWarshall();
        int INF = 1_000_000_000;

        for (String start : ALL_LOCATIONS) {
            for (String dest : ALL_LOCATIONS) {
                ShortPath.PathResult result = ShortPath.findShortestPath(city, start, dest);
                int expected = fw.get(start).get(dest);

                if (expected == INF) {
                    assertEquals("Unreachable pair must yield -1 for " + start + " -> " + dest, -1, result.totalDistance);
                    assertTrue("Unreachable path must be empty for " + start + " -> " + dest, result.path.isEmpty());
                } else {
                    assertEquals("Dijkstra distance must equal Floyd-Warshall optimal for " + start + " -> " + dest, 
                                 expected, result.totalDistance);
                    assertFalse("Path cannot be empty for reachable pair " + start + " -> " + dest, result.path.isEmpty());
                    assertEquals("Path must start with start node", start, result.path.get(0));
                    assertEquals("Path must end with destination node", dest, result.path.get(result.path.size() - 1));
                    assertEquals("Sum of edge weights must equal returned distance", expected, computePathDistance(result.path));
                }
            }
        }
    }

    // ==========================================
    // 3. EDGE CASES & INVARIANTS
    // ==========================================

    @Test
    public void testSelfDistanceIsZero() {
        for (String loc : ALL_LOCATIONS) {
            ShortPath.PathResult res = ShortPath.findShortestPath(city, loc, loc);
            assertEquals("Distance from a node to itself must be 0 for " + loc, 0, res.totalDistance);
            assertEquals("Path from a node to itself must have size 1", 1, res.path.size());
            assertEquals("Path from a node to itself must contain that node", loc, res.path.get(0));
        }
    }

    @Test
    public void testSymmetryInUndirectedGraph() {
        for (String u : ALL_LOCATIONS) {
            for (String v : ALL_LOCATIONS) {
                ShortPath.PathResult uv = ShortPath.findShortestPath(city, u, v);
                ShortPath.PathResult vu = ShortPath.findShortestPath(city, v, u);
                assertEquals("Undirected graph distance must be symmetric for " + u + " <-> " + v, 
                             uv.totalDistance, vu.totalDistance);
            }
        }
    }

    @Test
    public void testLeafNodesSingleExit() {
        // Clair's House connects only to Mall (4 km)
        ShortPath.PathResult resClair = ShortPath.findShortestPath(city, "clairsHouse", "mall");
        assertEquals(4, resClair.totalDistance);
        assertEquals(Arrays.asList("clairsHouse", "mall"), resClair.path);

        // Any route from Clair's House to another node MUST have 'mall' as second node
        ShortPath.PathResult resClairJim = ShortPath.findShortestPath(city, "clairsHouse", "jimsHouse");
        assertEquals("clairsHouse", resClairJim.path.get(0));
        assertEquals("mall", resClairJim.path.get(1));

        // Charlie's House connects only to Office (3 km)
        ShortPath.PathResult resCharlie = ShortPath.findShortestPath(city, "charliesHouse", "office");
        assertEquals(3, resCharlie.totalDistance);
        assertEquals(Arrays.asList("charliesHouse", "office"), resCharlie.path);

        // Any route from Charlie's House to another node MUST have 'office' as second node
        ShortPath.PathResult resCharlieJim = ShortPath.findShortestPath(city, "charliesHouse", "jimsHouse");
        assertEquals("charliesHouse", resCharlieJim.path.get(0));
        assertEquals("office", resCharlieJim.path.get(1));
    }
}
