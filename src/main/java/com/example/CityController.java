package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.StringConverter;

import java.util.*;

public class CityController {

    @FXML private ChoiceBox<String> startChoice;
    @FXML private ChoiceBox<String> endChoice;
    @FXML private Button findPathBtn;
    @FXML private Label resultLabel;

    // Nodes (Buildings)
    @FXML private Rectangle johnsHouse, hospital, school, mall, police, fireStation, office, sarahsHouse, park, jimsHouse;
    @FXML private Rectangle abandonedHouse, clairsHouse, charliesHouse, residentialBuilding;

    // Roads (Lines)
    @FXML private Line road_johns_sarahs, road_johns_school;
    @FXML private Line road_sarahs_office, road_sarahs_park;
    @FXML private Line road_hospital_school, road_hospital_park;
    @FXML private Line road_school_mall;
    @FXML private Line road_park_office, road_park_mall, road_park_police;
    @FXML private Line road_mall_police;
    @FXML private Line road_police_jim;
    @FXML private Line road_office_fire, road_fire_police;

    // New Roads
    @FXML private Line road_mall_clair;
    @FXML private Line road_office_charlie;
    @FXML private Line road_school_residential, road_residential_mall, road_residential_office;

    private Map<String, Shape> nodeMap = new HashMap<>();
    private Map<String, Paint> originalNodeColors = new HashMap<>();
    
    private Map<String, Line> roadMap = new HashMap<>();
    private Map<String, Paint> originalRoadColors = new HashMap<>();

    private CityGraph cityGraph;

    @FXML
    public void initialize() {
        cityGraph = new CityGraph();
        cityGraph.buildCity();

        // 1. Map Nodes
        nodeMap.put("johnsHouse", johnsHouse);
        nodeMap.put("hospital", hospital);
        nodeMap.put("school", school);
        nodeMap.put("mall", mall);
        nodeMap.put("police", police);
        nodeMap.put("fireStation", fireStation);
        nodeMap.put("office", office);
        nodeMap.put("sarahsHouse", sarahsHouse);
        nodeMap.put("park", park);
        nodeMap.put("jimsHouse", jimsHouse);
        nodeMap.put("abandonedHouse", abandonedHouse);
        nodeMap.put("clairsHouse", clairsHouse);
        nodeMap.put("charliesHouse", charliesHouse);
        nodeMap.put("residentialBuilding", residentialBuilding);

        // Save original node colors
        for (Map.Entry<String, Shape> entry : nodeMap.entrySet()) {
            originalNodeColors.put(entry.getKey(), entry.getValue().getFill());
        }

        // 2. Map Roads
        roadMap.put("johnsHouse-sarahsHouse", road_johns_sarahs);
        roadMap.put("johnsHouse-school", road_johns_school);
        
        roadMap.put("sarahsHouse-office", road_sarahs_office);
        roadMap.put("sarahsHouse-park", road_sarahs_park);
        
        roadMap.put("hospital-school", road_hospital_school);
        roadMap.put("hospital-park", road_hospital_park);
        
        roadMap.put("school-mall", road_school_mall);
        
        roadMap.put("park-office", road_park_office);
        roadMap.put("park-mall", road_park_mall);
        roadMap.put("park-police", road_park_police);
        
        roadMap.put("mall-police", road_mall_police);
        
        roadMap.put("police-jimsHouse", road_police_jim);
        
        roadMap.put("office-fireStation", road_office_fire);
        roadMap.put("fireStation-police", road_fire_police);

        // New road mappings
        roadMap.put("mall-clairsHouse", road_mall_clair);
        roadMap.put("office-charliesHouse", road_office_charlie);
        roadMap.put("school-residentialBuilding", road_school_residential);
        roadMap.put("residentialBuilding-mall", road_residential_mall);
        roadMap.put("residentialBuilding-office", road_residential_office);

        // Save original road colors
        for (Map.Entry<String, Line> entry : roadMap.entrySet()) {
            originalRoadColors.put(entry.getKey(), entry.getValue().getStroke());
        }

        // 3. Populate Dropdowns
        List<String> locations = Arrays.asList(
            "johnsHouse", "sarahsHouse", "jimsHouse", "clairsHouse", "charliesHouse",
            "residentialBuilding", "abandonedHouse",
            "hospital", "school", "mall", "police", "fireStation", "office", "park"
        );
        startChoice.getItems().addAll(locations);
        endChoice.getItems().addAll(locations);
        
        StringConverter<String> converter = new StringConverter<>() {
            @Override
            public String toString(String object) {
                return object == null ? "" : formatName(object);
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        };
        startChoice.setConverter(converter);
        endChoice.setConverter(converter);

        startChoice.setValue("johnsHouse");
        endChoice.setValue("park");
    }

    @FXML
    private void handleFindPath() {
        String start = startChoice.getValue();
        String end = endChoice.getValue();

        if (start == null || end == null) {
            resultLabel.setText("Select locations.");
            return;
        }

        resetVisuals();

        ShortPath.PathResult result = ShortPath.findShortestPath(cityGraph, start, end);

        if (result.totalDistance == -1) {
            resultLabel.setText("No path found between\n" + formatName(start) + "\nand\n" + formatName(end) + ".");
        } else {
            List<String> displayPath = new ArrayList<>();
            for (String loc : result.path) {
                displayPath.add(formatName(loc));
            }
            
            resultLabel.setText("Distance: " + result.totalDistance + " km\n\nPath:\n" + String.join(" ➔ ", displayPath));
            highlightRoute(result.path);
        }
    }

    // Helper to format internal location IDs to user-friendly names
    private String formatName(String internalName) {
        switch (internalName) {
            case "johnsHouse": return "John's House";
            case "sarahsHouse": return "Sarah's House";
            case "jimsHouse": return "Jim's House";
            case "clairsHouse": return "Clair's House";
            case "charliesHouse": return "Charlie's House";
            case "abandonedHouse": return "Abandoned House";
            case "residentialBuilding": return "Residential Building";
            case "hospital": return "Hospital";
            case "school": return "School";
            case "mall": return "Mall";
            case "police": return "Police";
            case "fireStation": return "Fire Station";
            case "office": return "Office";
            case "park": return "Park";
            default: return internalName;
        }
    }

    private void highlightRoute(List<String> path) {
        Color highlightColor = Color.web("#FF4757");

        // Highlight Nodes
        for (String loc : path) {
            Shape shape = nodeMap.get(loc);
            if (shape != null) {
                shape.setFill(highlightColor);
            }
        }

        // Highlight Roads
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            
            // Check both directions for the road key
            Line road = roadMap.get(from + "-" + to);
            if (road == null) {
                road = roadMap.get(to + "-" + from);
            }

            if (road != null) {
                road.setStroke(highlightColor);
                road.setStrokeWidth(6.0);
            }
        }
    }

    private void resetVisuals() {
        // Reset Nodes
        for (Map.Entry<String, Shape> entry : nodeMap.entrySet()) {
            entry.getValue().setFill(originalNodeColors.get(entry.getKey()));
        }
        // Reset Roads
        for (Map.Entry<String, Line> entry : roadMap.entrySet()) {
            entry.getValue().setStroke(originalRoadColors.get(entry.getKey()));
            entry.getValue().setStrokeWidth(4.0); // Reset to original width
        }
        resultLabel.setText("");
    }

    // ===== 6 PRESET TEST CASES =====

    @FXML
    private void handleTestCase1() {
        // Test 1: John's House → Jim's House (cross-map route)
        startChoice.setValue("johnsHouse");
        endChoice.setValue("jimsHouse");
        handleFindPath();
    }

    @FXML
    private void handleTestCase2() {
        // Test 2: Hospital → Police (cross-map service route)
        startChoice.setValue("hospital");
        endChoice.setValue("police");
        handleFindPath();
    }

    @FXML
    private void handleTestCase3() {
        // Test 3: Sarah's House → Mall (shopping route)
        startChoice.setValue("sarahsHouse");
        endChoice.setValue("mall");
        handleFindPath();
    }

    @FXML
    private void handleTestCase4() {
        // Test 4: Clair's House → Charlie's House (traversing across new house nodes)
        startChoice.setValue("clairsHouse");
        endChoice.setValue("charliesHouse");
        handleFindPath();
    }

    @FXML
    private void handleTestCase5() {
        // Test 5: Residential Building → Jim's House (route from residential building)
        startChoice.setValue("residentialBuilding");
        endChoice.setValue("jimsHouse");
        handleFindPath();
    }

    @FXML
    private void handleTestCase6() {
        // Test 6: Abandoned House → Mall (isolated node test: No path found)
        startChoice.setValue("abandonedHouse");
        endChoice.setValue("mall");
        handleFindPath();
    }
}
