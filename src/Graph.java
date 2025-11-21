import java.util.*;

/**
 * Graph class that supports both directed and undirected graphs.
 * Implements both Adjacency List and Adjacency Matrix representations.
 */
public class Graph {
    private int numVertices;
    private boolean isDirected;
    private Map<String, Integer> vertexIndex; // Maps vertex names to indices
    private Map<Integer, String> indexVertex; // Maps indices back to vertex names

    // Map<Integer, List<Edge>> where Integer is the source vertex index
    private Map<Integer, List<Edge>> adjacencyList;

    // double[][] where matrix[i][j] represents edge weight from vertex i to j
    private double[][] adjacencyMatrix;

    /**
     * Inner class to represent an edge with destination and weight.
     */
    public static class Edge {
        int destination;
        double weight;

        public Edge(int destination, double weight) {
            this.destination = destination;
            this.weight = weight;
        }

        public int getDestination() { return destination; }
        public double getWeight() { return weight; }

        @Override
        public String toString() {
            return "(" + destination + ", w:" + weight + ")";
        }
    }

    /**
     * Constructor: Initialize the graph structures.
     * @param vertices List of vertex names
     * @param isDirected Whether the graph is directed
     */
    public Graph(List<String> vertices, boolean isDirected) {
        this.numVertices = vertices.size();
        this.isDirected = isDirected;
        this.vertexIndex = new HashMap<>();
        this.indexVertex = new HashMap<>();

        // Map vertex names to indices
        for (int i = 0; i < vertices.size(); i++) {
            vertexIndex.put(vertices.get(i), i);
            indexVertex.put(i, vertices.get(i));
        }

        // TODO: Initialize Adjacency List
        // 1. Instantiate the Map
        // 2. Loop through 0 to numVertices-1 and add an empty ArrayList for each vertex


        HashMap<Integer, ArrayList> adjacencyList = new HashMap<>();

        for(int i = 0; i < numVertices - 1; i++)
        {
            adjacencyList.put(i, new ArrayList<>());
        }


        // TODO: Initialize Adjacency Matrix
        // 1. Instantiate the 2D array [numVertices][numVertices]
        // 2. IMPORTANT: Java arrays default to 0.0. You must loop through the matrix
        //    and set every value to Double.POSITIVE_INFINITY to represent "No Edge".

        double[][] adjacencyMatrix = new double[numVertices][numVertices];

        for(int i = 0; i < numVertices - 1; i++)
        {
            for(int j = 0; i < numVertices - 1; j++)
            {
                adjacencyMatrix[i][j] = Double.POSITIVE_INFINITY;
            }
        }


    }

    /**
     * Add an edge to the graph.
     * @param source Source vertex name
     * @param destination Destination vertex name
     * @param weight Edge weight
     */
    public void addEdge(String source, String destination, double weight) {
        // Validation: Ensure vertices exist
        if (!vertexIndex.containsKey(source) || !vertexIndex.containsKey(destination)) {
            throw new IllegalArgumentException("One or more vertices not found in graph.");
        }

        int srcIdx = vertexIndex.get(source);
        int destIdx = vertexIndex.get(destination);

        // TODO: Add edge to Adjacency List
        // Create a new Edge object and add it to the list for srcIdx

        Edge edge = new Edge(destIdx, srcIdx);
        List edgeList = adjacencyList.get(srcIdx);
        edgeList.add(edge);
        adjacencyList.put(srcIdx, edgeList);

        // TODO: Add edge to Adjacency Matrix
        // Update matrix[srcIdx][destIdx] with the weight

        adjacencyMatrix[srcIdx][destIdx] = weight;

        // TODO: Handle Undirected Graphs
        // If (!isDirected), you must explicitly add the reverse edge (destination -> source)
        // to both the list and the matrix.

        if(!isDirected)
        {
            Edge edge2 = new Edge(destIdx, srcIdx);
            List edgeList2 = adjacencyList.get(srcIdx);
            edgeList2.add(edge2);
            adjacencyList.put(srcIdx, edgeList2);
            adjacencyMatrix[destIdx][srcIdx] = weight;
        }
        else if(Math.random() <= 0.75)
        {

        }



    }

    /**
     * Perform Breadth-First Search (BFS).
     * Traverses the graph layer-by-layer using a Queue.
     * @param startVertex Name of the starting vertex
     * @return List of vertex names in BFS order
     */
    public List<String> bfs(String startVertex) {
        if (!vertexIndex.containsKey(startVertex)) return new ArrayList<>();

        List<String> result = new ArrayList<>();

        // TODO: Implement BFS
        // 1. Create a Queue<Integer> and a boolean[] visited array.
        // 2. Add start node index to queue and mark visited.
        // 3. While queue is not empty:
        //    - Poll the current vertex index.
        //    - Add the vertex name to 'result'.
        //    - Loop through its neighbors (from adjacencyList).
        //    - If neighbor is not visited: mark visited and add to queue.






        return result;
    }

    /**
     * Perform Depth-First Search (DFS).
     * Traverses the graph by going as deep as possible using a Stack (or recursion).
     * @param startVertex Name of the starting vertex
     * @return List of vertex names in DFS order
     */
    public List<String> dfs(String startVertex) {
        if (!vertexIndex.containsKey(startVertex)) return new ArrayList<>();

        List<String> result = new ArrayList<>();
        boolean[] visited = new boolean[numVertices];

        // TODO: Implement DFS
        // You can do this Iteratively (Stack<Integer>) or Recursively (helper method).
        // If Recursive: dfsHelper(currentIdx, visited, result)






        return result;
    }

    // ==========================================================
    // HELPER & VISUALIZER METHODS (Do not modify)
    // ==========================================================

    public List<Edge> getNeighbors(String vertex) {
        int idx = vertexIndex.get(vertex);
        return adjacencyList.get(idx);
    }

    public double[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public String getVertexName(int index) {
        return indexVertex.get(index);
    }

    public int getVertexIndex(String name) {
        return vertexIndex.get(name);
    }

    public int getNumVertices() {
        return numVertices;
    }

    public boolean isDirected() {
        return isDirected;
    }

    public Set<String> getVertexNames() {
        return vertexIndex.keySet();
    }

    public void printAdjacencyList() {
        System.out.println("\n--- Adjacency List ---");
        for (int i = 0; i < numVertices; i++) {
            System.out.print(indexVertex.get(i) + " -> ");
            List<Edge> neighbors = adjacencyList.get(i);
            if (neighbors.isEmpty()) {
                System.out.print("[No Neighbors]");
            } else {
                for (Edge edge : neighbors) {
                    System.out.print(indexVertex.get(edge.destination) + "(" + edge.weight + ") ");
                }
            }
            System.out.println();
        }
    }

    public void printAdjacencyMatrix() {
        System.out.println("\n--- Adjacency Matrix ---");
        System.out.print("        ");
        for (int i = 0; i < numVertices; i++) {
            System.out.printf("%8s", indexVertex.get(i));
        }
        System.out.println();

        for (int i = 0; i < numVertices; i++) {
            System.out.printf("%-8s", indexVertex.get(i));
            for (int j = 0; j < numVertices; j++) {
                double val = adjacencyMatrix[i][j];
                if (val == Double.POSITIVE_INFINITY) {
                    System.out.printf("%8s", "INF");
                } else {
                    System.out.printf("%8.1f", val);
                }
            }
            System.out.println();
        }
    }
}