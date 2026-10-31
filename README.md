# 🗺️ City Route Finder - Data Structures Mini Project

An interactive desktop application built with **Java** and **JavaFX** that visualizes **Dijkstra's Shortest Path Algorithm** on an interconnected city map graph.

---

## 📌 Project Overview

**City Route Finder** models a city as a weighted, undirected graph where:
- **Vertices (Nodes)** represent buildings and landmarks (houses, school, mall, hospital, office, park, etc.).
- **Edges (Roads)** represent road segments with weights denoting real-world distances in kilometers (km).
- **Shortest Path Engine** computes the optimal route between any two selected locations using Dijkstra's Algorithm implemented with a Min-Heap Priority Queue.

---

## ✨ Features

- **Interactive Pathfinding**: Select any starting point and destination to find the shortest route with total distance in kilometers and step-by-step path sequence (`Start ➔ ... ➔ Destination`).
- **Real-Time Visual Highlighting**: The computed optimal route and its road segments dynamically illuminate in vibrant cyan.
- **Dark-Themed Modern UI**: Clean JavaFX canvas with distinct color-coded buildings, customized ChoiceBoxes, and polished road labels.
- **Special Graph Topologies**:
  - **🏚️ Abandoned House**: An isolated node with no road connections. Used to demonstrate unreachable state handling in Dijkstra's algorithm (*"No path found"*).
  - **🏠 Clair's House**: A residential house connected exclusively to the Mall.
  - **🏠 Charlie's House**: A residential house connected exclusively to the Office.
  - **🏢 Residential Building**: A central multi-connected hub connected to the Mall, School, and Office.
- **Preset Test Cases**: 6 quick-access buttons in the sidebar for rapid demonstration:
  1. `John's House ➔ Jim's House` (Longest cross-map route)
  2. `Hospital ➔ Police` (Service/emergency route)
  3. `Sarah's House ➔ Mall` (Shopping route)
  4. `Clair's House ➔ Charlie's House` (Route across newly connected houses)
  5. `Residential Building ➔ Jim's House` (Route starting from the residential hub)
  6. `Abandoned House ➔ Mall` (Demonstrates unreachable node with no path)

---

## 🛠️ Tech Stack & Architecture

- **Language**: Java 11+
- **GUI Framework**: JavaFX (Controls & FXML)
- **Build Tool**: Apache Maven
- **Core Algorithm**: Dijkstra's Algorithm ($O((V + E) \log V)$ time complexity using `PriorityQueue`)

### Key Classes

| File | Description |
|---|---|
| [`CityGraph.java`](src/main/java/com/example/CityGraph.java) | Adjacency list representation of the city graph, locations, and weighted edges |
| [`ShortPath.java`](src/main/java/com/example/ShortPath.java) | Dijkstra's shortest path algorithm implementation using a Priority Queue |
| [`CityController.java`](src/main/java/com/example/CityController.java) | Controller managing UI interactions, path triggers, visual route resets/highlights, and test presets |
| [`App.java`](src/main/java/com/example/App.java) | JavaFX Application entry point |
| [`primary.fxml`](src/main/resources/com/example/primary.fxml) | FXML layout definition for the map canvas and sidebar panel |
| [`style.css`](src/main/resources/com/example/style.css) | Custom styling for the dark theme, context menus, and controls |

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 11 or higher
- **Apache Maven**: Version 3.6 or higher

### Installation & Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Amaanstilcodes/DsMiniProject.git
   cd DsMiniProject
   ```

2. **Compile the project**:
   ```bash
   mvn clean compile
   ```

3. **Launch the application**:
   ```bash
   mvn javafx:run
   ```

---

## 🧪 Graph Layout

```
Abandoned House (Isolated)

School (380, 50)  ==== 3 km ====  Mall (620, 50)  ==== 4 km ====  Clair's House (850, 50)
     ||                                 ||
    3 km                               4 km
     ||                                 ||
Residential Bldg (380, 210) ============//
     ||
    5 km
     ||
   Office (380, 470)  ==== 9 km ====  Fire Station (620, 470)
     ||                                       \\ 6 km
    3 km                                       \\
     ||                                         Police (810, 320) ==== 5 km ==== Jim's House (870, 495)
Charlie's House (380, 615)
```

---

## 📄 License

This project is created for educational and academic demonstration purposes as part of the Data Structures course curriculum.
