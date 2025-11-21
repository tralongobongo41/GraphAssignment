import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * GUI for visualizing graph operations.
 * Updated to work with the improved Graph class (Infinity checks, DFS support).
 */
public class GraphVisualizer extends JFrame {
    private Graph graph;
    private GraphPanel graphPanel;
    private JTextArea outputArea;
    private JComboBox<String> startVertexCombo;
    private JCheckBox directedCheckBox;
    private Map<String, Point> vertexPositions;

    // Colors for visualization
    private static final Color DEFAULT_VERTEX_COLOR = new Color(52, 152, 219); // Blue
    private static final Color VISITED_VERTEX_COLOR = new Color(46, 204, 113); // Green
    private static final Color CURRENT_VERTEX_COLOR = new Color(231, 76, 60);  // Red
    private static final Color DEFAULT_EDGE_COLOR = new Color(149, 165, 166);  // Gray

    public GraphVisualizer() {
        setTitle("Graph Algorithm Visualizer");
        setSize(1250, 850); // Slightly wider for the extra button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize vertex positions for the campus map
        initializeVertexPositions();

        // Create control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        // Create graph panel
        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        // Create output panel
        JPanel outputPanel = createOutputPanel();
        add(outputPanel, BorderLayout.SOUTH);

        // Initialize graph
        initializeGraph(false);

        // Force everything to render
        validate();
        repaint();
    }

    /**
     * Initialize positions for each vertex (campus layout)
     */
    private void initializeVertexPositions() {
        vertexPositions = new HashMap<>();

        // Approximate positions based on the campus map image
        vertexPositions.put("Gate", new Point(400, 50));
        vertexPositions.put("Student Parking", new Point(200, 120));
        vertexPositions.put("Senior Parking", new Point(600, 120));
        vertexPositions.put("Circle", new Point(400, 200));
        vertexPositions.put("Admissions", new Point(550, 250));
        vertexPositions.put("Business Office", new Point(700, 250));
        vertexPositions.put("Athletics", new Point(100, 300));
        vertexPositions.put("PA", new Point(250, 350));
        vertexPositions.put("Cohen", new Point(400, 380));
        vertexPositions.put("Fountain", new Point(550, 350));
        vertexPositions.put("US 100", new Point(700, 350));
        vertexPositions.put("Faculty Parking", new Point(900, 350));
        vertexPositions.put("Library", new Point(600, 450));
        vertexPositions.put("CHH", new Point(750, 500));
        vertexPositions.put("VA", new Point(850, 550));
        vertexPositions.put("Mariani", new Point(400, 550));
        vertexPositions.put("Science", new Point(250, 650));
        vertexPositions.put("BD", new Point(400, 700));
        vertexPositions.put("GD", new Point(850, 700));
    }

    /**
     * Create the control panel with buttons and options
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(44, 62, 80));
        panel.setPreferredSize(new Dimension(1200, 60));

        // Directed graph checkbox
        directedCheckBox = new JCheckBox("Directed Graph", false);
        directedCheckBox.setForeground(Color.WHITE);
        directedCheckBox.setBackground(new Color(44, 62, 80));
        directedCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        directedCheckBox.setFocusPainted(false);
        directedCheckBox.addActionListener(e -> initializeGraph(directedCheckBox.isSelected()));
        panel.add(directedCheckBox);

        panel.add(Box.createHorizontalStrut(20));

        // Start vertex selector
        JLabel startLabel = new JLabel("Start Vertex:");
        startLabel.setForeground(Color.WHITE);
        startLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(startLabel);

        startVertexCombo = new JComboBox<>();
        startVertexCombo.setPreferredSize(new Dimension(150, 30));
        panel.add(startVertexCombo);

        panel.add(Box.createHorizontalStrut(10));

        // BFS Button
        JButton bfsButton = createStyledButton("Run BFS", new Color(52, 152, 219));
        bfsButton.addActionListener(e -> runBFS());
        panel.add(bfsButton);

        // DFS Button (NEW)
        JButton dfsButton = createStyledButton("Run DFS", new Color(46, 204, 113));
        dfsButton.addActionListener(e -> runDFS());
        panel.add(dfsButton);

        // Show Adjacency List Button
        JButton adjListButton = createStyledButton("Show Adj List", new Color(155, 89, 182));
        adjListButton.addActionListener(e -> showAdjacencyList());
        panel.add(adjListButton);

        // Show Adjacency Matrix Button
        JButton adjMatrixButton = createStyledButton("Show Matrix", new Color(230, 126, 34));
        adjMatrixButton.addActionListener(e -> showAdjacencyMatrix());
        panel.add(adjMatrixButton);

        // Reset Button
        JButton resetButton = createStyledButton("Reset", new Color(231, 76, 60));
        resetButton.addActionListener(e -> reset());
        panel.add(resetButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return btn;
    }

    /**
     * Create the output panel for displaying results
     */
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(1200, 150));

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setBackground(new Color(39, 55, 70));
        outputArea.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Initialize or reinitialize the graph with edges
     */
    private void initializeGraph(boolean directed) {
        List<String> vertices = Arrays.asList(
                "Gate", "Student Parking", "Senior Parking", "Circle", "Admissions",
                "Business Office", "Athletics", "PA", "Cohen", "Fountain", "US 100",
                "Faculty Parking", "Library", "CHH", "VA", "Mariani", "Science", "BD", "GD"
        );

        try {
            graph = new Graph(vertices, directed);

            String[][] edges = {
                    {"Gate", "Student Parking"}, {"Gate", "Circle"}, {"Gate", "Senior Parking"},
                    {"Student Parking", "Circle"}, {"Student Parking", "Athletics"}, {"Student Parking", "PA"},
                    {"Senior Parking", "Circle"}, {"Senior Parking", "Admissions"}, {"Senior Parking", "Business Office"},
                    {"Circle", "Admissions"}, {"Circle", "PA"}, {"Circle", "Cohen"},
                    {"Admissions", "Fountain"}, {"Admissions", "Business Office"},
                    {"Business Office", "US 100"}, {"Business Office", "Fountain"},
                    {"Athletics", "PA"}, {"Athletics", "Science"},
                    {"PA", "Cohen"}, {"PA", "Mariani"}, {"PA", "Science"},
                    {"Cohen", "Mariani"}, {"Cohen", "Fountain"}, {"Cohen", "Library"},
                    {"Fountain", "US 100"}, {"Fountain", "Library"},
                    {"US 100", "Library"}, {"US 100", "Faculty Parking"}, {"US 100", "CHH"},
                    {"Faculty Parking", "VA"}, {"Faculty Parking", "CHH"},
                    {"Library", "CHH"}, {"Library", "Mariani"},
                    {"CHH", "VA"}, {"CHH", "GD"},
                    {"VA", "GD"}, {"VA", "Mariani"},
                    {"Mariani", "Science"}, {"Mariani", "BD"}, {"Mariani", "GD"},
                    {"Science", "BD"}, {"BD", "GD"}
            };

            Random rand = new Random(42);
            for (String[] edge : edges) {
                double weight = 1.0 + rand.nextInt(8);
                graph.addEdge(edge[0], edge[1], weight);
            }

            // Update start vertex combo box
            startVertexCombo.removeAllItems();
            for (String vertex : vertices) {
                startVertexCombo.addItem(vertex);
            }

            reset();
            outputArea.setText("Graph loaded successfully!\n");
        } catch (Exception e) {
            outputArea.setText("?? Graph implementation not complete!\n");
            outputArea.append("Complete the TODOs in Graph.java to enable visualization.\n");

            // Still populate the combo box with vertices so UI doesn't look broken
            startVertexCombo.removeAllItems();
            for (String vertex : vertices) {
                startVertexCombo.addItem(vertex);
            }
        }
    }

    private void runBFS() {
        runTraversal(true);
    }

    private void runDFS() {
        runTraversal(false);
    }

    /**
     * Helper to run BFS or DFS and animate
     */
    private void runTraversal(boolean isBFS) {
        String startVertex = (String) startVertexCombo.getSelectedItem();
        if (startVertex == null || graph == null) return;

        try {
            String algName = isBFS ? "BFS" : "DFS";
            outputArea.setText("Running " + algName + " from " + startVertex + "...\n");

            // Call the appropriate method from Graph class
            List<String> order = isBFS ? graph.bfs(startVertex) : graph.dfs(startVertex);

            if (order == null || order.isEmpty()) {
                outputArea.setText("?? " + algName + " returned no results. Check your implementation.\n");
                return;
            }

            // Animation Logic
            reset(); // Clear previous colors
            javax.swing.Timer timer = new javax.swing.Timer(600, null);
            final int[] index = {0};

            timer.addActionListener(e -> {
                if (index[0] < order.size()) {
                    String vertex = order.get(index[0]);
                    graphPanel.setCurrentVertex(vertex);
                    outputArea.append((index[0] + 1) + ". " + vertex + "\n");
                    index[0]++;
                } else {
                    timer.stop();
                    outputArea.append("\n" + algName + " Complete!\n");
                    outputArea.append("Path: " + String.join(" -> ", order) + "\n");
                }
            });
            timer.start();

        } catch (Exception e) {
            outputArea.setText("? Error running algorithm!\n");
            outputArea.append("Exception: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void showAdjacencyList() {
        if (graph == null) return;
        try {
            outputArea.setText("ADJACENCY LIST REPRESENTATION\n");
            outputArea.append("================================\n\n");

            for (String vertex : graph.getVertexNames()) {
                outputArea.append(String.format("%-15s -> ", vertex));
                List<Graph.Edge> neighbors = graph.getNeighbors(vertex);

                if (neighbors == null) {
                    outputArea.append("[null list]");
                } else if (neighbors.isEmpty()) {
                    outputArea.append("[No Neighbors]");
                } else {
                    for (int i = 0; i < neighbors.size(); i++) {
                        Graph.Edge edge = neighbors.get(i);
                        String destName = graph.getVertexName(edge.destination);
                        outputArea.append(destName + "(" + String.format("%.0f", edge.weight) + ")");
                        if (i < neighbors.size() - 1) outputArea.append(", ");
                    }
                }
                outputArea.append("\n");
            }
        } catch (Exception e) {
            outputArea.setText("? Error displaying list. Check Adjacency List implementation.\n" + e.getMessage());
        }
    }

    private void showAdjacencyMatrix() {
        if (graph == null) return;
        try {
            outputArea.setText("ADJACENCY MATRIX REPRESENTATION\n");
            outputArea.append("=================================\n\n");

            double[][] matrix = graph.getAdjacencyMatrix();
            if (matrix == null) {
                outputArea.append("?? Matrix is null. Check constructor.\n");
                return;
            }

            int n = graph.getNumVertices();

            // Header
            outputArea.append(String.format("%-15s", ""));
            for (int i = 0; i < n; i++) {
                String name = graph.getVertexName(i);
                outputArea.append(String.format("%-8s", name.substring(0, Math.min(6, name.length()))));
            }
            outputArea.append("\n");

            // Rows
            for (int i = 0; i < n; i++) {
                outputArea.append(String.format("%-15s", graph.getVertexName(i)));
                for (int j = 0; j < n; j++) {
                    double val = matrix[i][j];
                    // UPDATED: Check for Infinity
                    if (val == Double.POSITIVE_INFINITY) {
                        outputArea.append(String.format("%-8s", "INF"));
                    } else {
                        outputArea.append(String.format("%-8.1f", val));
                    }
                }
                outputArea.append("\n");
            }
        } catch (Exception e) {
            outputArea.setText("? Error displaying matrix. Check Matrix implementation.\n" + e.getMessage());
        }
    }

    private void reset() {
        graphPanel.reset();
        outputArea.setText("Ready. Select a starting vertex and choose an operation.\n");
    }

    /**
     * Inner class for drawing the graph
     */
    class GraphPanel extends JPanel {
        private Set<String> visitedVertices;
        private String currentVertex;

        public GraphPanel() {
            setBackground(new Color(245, 245, 245));
            visitedVertices = new HashSet<>();
        }

        public void setCurrentVertex(String vertex) {
            visitedVertices.add(vertex);
            currentVertex = vertex;
            repaint();
        }

        public void reset() {
            visitedVertices.clear();
            currentVertex = null;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (graph == null) return;

            // 1. Draw Edges
            try {
                Collection<String> vertices = graph.getVertexNames();
                if (vertices == null) return;

                for (String vertex : vertices) {
                    Point start = vertexPositions.get(vertex);
                    List<Graph.Edge> neighbors = graph.getNeighbors(vertex);

                    if (start == null || neighbors == null) continue;

                    for (Graph.Edge edge : neighbors) {
                        String destName = graph.getVertexName(edge.destination);
                        Point end = vertexPositions.get(destName);

                        if (end == null) continue;

                        // In undirected mode, only draw edge once to avoid text overlap
                        if (!graph.isDirected() && vertex.compareTo(destName) > 0) continue;

                        g2d.setColor(DEFAULT_EDGE_COLOR);
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawLine(start.x, start.y, end.x, end.y);

                        if (graph.isDirected()) {
                            drawArrow(g2d, start, end);
                        }

                        // Draw weight
                        int midX = (start.x + end.x) / 2;
                        int midY = (start.y + end.y) / 2;
                        g2d.setColor(Color.BLACK);
                        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                        // Draw a small white box behind text for readability
                        String wStr = String.format("%.0f", edge.weight);
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(midX, midY-10, 15, 12);
                        g2d.setColor(Color.BLACK);
                        g2d.drawString(wStr, midX+2, midY);
                    }
                }
            } catch (Exception e) {
                // Swallow exceptions during painting (e.g. partial student implementation)
            }

            // 2. Draw Vertices
            try {
                for (String vertex : graph.getVertexNames()) {
                    Point p = vertexPositions.get(vertex);
                    if (p == null) continue;

                    if (vertex.equals(currentVertex)) {
                        g2d.setColor(CURRENT_VERTEX_COLOR);
                    } else if (visitedVertices.contains(vertex)) {
                        g2d.setColor(VISITED_VERTEX_COLOR);
                    } else {
                        g2d.setColor(DEFAULT_VERTEX_COLOR);
                    }

                    int r = 12; // radius
                    g2d.fillOval(p.x - r, p.y - r, 2 * r, 2 * r);

                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(p.x - r, p.y - r, 2 * r, 2 * r);

                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    g2d.drawString(vertex, p.x - r, p.y - r - 5);
                }
            } catch (Exception e) { }
        }

        private void drawArrow(Graphics2D g2d, Point start, Point end) {
            double angle = Math.atan2(end.y - start.y, end.x - start.x);
            int arrowSize = 8;
            int nodeRadius = 12;

            // Calculate tip of arrow (offset by node radius)
            int tipX = (int) (end.x - nodeRadius * Math.cos(angle));
            int tipY = (int) (end.y - nodeRadius * Math.sin(angle));

            // Calculate wings
            int x1 = (int) (tipX - arrowSize * Math.cos(angle - Math.PI / 6));
            int y1 = (int) (tipY - arrowSize * Math.sin(angle - Math.PI / 6));
            int x2 = (int) (tipX - arrowSize * Math.cos(angle + Math.PI / 6));
            int y2 = (int) (tipY - arrowSize * Math.sin(angle + Math.PI / 6));

            g2d.fillPolygon(new int[]{tipX, x1, x2}, new int[]{tipY, y1, y2}, 3);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphVisualizer visualizer = new GraphVisualizer();
            visualizer.setVisible(true);
        });
    }
}