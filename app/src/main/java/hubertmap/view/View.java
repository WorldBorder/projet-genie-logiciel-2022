package hubertmap.view;

import edu.uci.ics.jung.graph.util.Pair;
import hubertmap.controller.Controller;
import hubertmap.model.Time;
import hubertmap.model.transport.EdgeTransport;
import hubertmap.model.transport.Line;
import hubertmap.model.transport.Station;
import hubertmap.model.transport.VertexTransport;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * The View class represents the graphical user interface of the application, and contains a Panel
 * instance for displaying the main content of the GUI.
 */
public class View {

    /** The Panel instance that displays the main content of the GUI. */
    private JPanel panel;

    /** A GraphPanelJung object for drawing the graph using the JUNG library. */
    private GraphPanel graphPanel;

    private JPanel leftPanel;

    private SchedulesPanel schedulesPanel;
    private JPanel generalPanel;

    private HashMap<String, Line> lines;

    private JFrame frame;

    private SearchPanel searchPanel;

    private Time currentTime;

    /**
     * Constructs a new View instance and initializes its components. Creates a JFrame window and
     * adds the Panel to it.
     *
     * @param graphView the the NetworkData with graph and informations to be displayed
     */
    public View(GraphData graphView) {
        panel = createPanel(graphView);

        frame = new JFrame("Hubertmap");

        String imagePath = "ressource/Hubert.png";
        try {
            BufferedImage icon = ImageIO.read(new File(imagePath));
            frame.setIconImage(icon);
        } catch (IOException e) {
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

    /**
     * Creates a panel containing a graph and a search panel.
     *
     * @param graphView the NetworkData object containing the graph data
     * @return the created panel
     */
    private JPanel createPanel(GraphData graphView) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        graphPanel = new GraphPanel(graphView);
        graphPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        this.lines = graphView.getLines();

        leftPanel = new JPanel(new BorderLayout());

        searchPanel = new SearchPanel(graphView);

        leftPanel.add(searchPanel, BorderLayout.NORTH);

        panel.add(leftPanel);

        panel.add(graphPanel);

        return panel;
    }

    /**
     * Sets the list of edges in the View that make up the shortest path
     *
     * @param time start time of travel
     * @param shortestPath List of edges to set in View
     */
    public void setShortestPath(LocalTime time, List<EdgeTransport> shortestPath) {
        graphPanel.getDecorator().setShortestPath(shortestPath);
        graphPanel.repaint();
        setPathDetails(time, shortestPath);
    }

    /**
     * Sets the name of the departure station to be displayed in the text area.
     *
     * @param name the name of the departure station to be displayed.
     */
    public void setDeparture(String name) {
        searchPanel.setDeparture(name);
    }

    /**
     * Sets the name of the arrival station to be displayed in the text area.
     *
     * @param name the name of the arrival station to be displayed.
     */
    public void setArrival(String name) {
        searchPanel.setArrival(name);
    }

    /**
     * Sets the schedules panel to display the schedules for the given station.
     *
     * @param v the station for which the schedules will be displayed.
     */
    public void setSchedules(Station v) {
        if (schedulesPanel != null) leftPanel.remove(schedulesPanel);
        if (generalPanel != null) leftPanel.remove(generalPanel);

        searchPanel.setText(v.getName());

        schedulesPanel = new SchedulesPanel(v, lines);
        leftPanel.add(schedulesPanel);

        leftPanel.revalidate();
    }

    /**
     * Creates panel with details of path
     *
     * @param time start time of travel
     * @param shortestPath shortes path to output
     */
    public void setPathDetails(LocalTime time, List<EdgeTransport> shortestPath) {
        if (generalPanel != null) leftPanel.remove(generalPanel);
        if (schedulesPanel != null) leftPanel.remove(schedulesPanel);

        generalPanel = new JPanel();
        generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
        generalPanel.setLayout(new GridLayout(0, 1, 5, 5));

        List<CustomPair> segments = getSegments(shortestPath);

        currentTime = new Time(time.getHour(), time.getMinute(), time.getSecond()); // TODO

        for (int i = 0; i < segments.size(); i += 2) {
            JPanel pathPanel =
                    createPathPanel(
                            shortestPath,
                            segments.get(i).getFirst(),
                            segments.get(i).getSecond(),
                            segments.get(i + 1).getSecond());

            generalPanel.add(pathPanel);
        }

        leftPanel.add(generalPanel);

        leftPanel.revalidate();
    }

    /**
     * Creates a panel with details of path between 2 stations
     *
     * @param shortestPath
     * @param line line number of given path
     * @param vertexTransport first station of path
     * @param vertexTransport2 last station of path
     */
    private JPanel createPathPanel(
            List<EdgeTransport> shortestPath,
            String lineName,
            VertexTransport vertexTransport,
            VertexTransport vertexTransport2) {

        Pair<Time> time = null;
        if (currentTime != null) {
            time =
                    Controller.getTimes(
                            shortestPath, vertexTransport, vertexTransport2, currentTime);
            if (time != null) currentTime = time.getSecond();
            else currentTime = null;
        }

        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.X_AXIS));

        JPanel startEndPanel = new JPanel();
        startEndPanel.setLayout(new BoxLayout(startEndPanel, BoxLayout.Y_AXIS));
        startEndPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));

        JPanel startPanel = new JPanel();
        startPanel.setLayout(new BorderLayout());
        startPanel.setPreferredSize(new Dimension(240, 20));
        String startLabelText = "";
        if (time != null) {
            startLabelText = time.getFirst().toString() + " - ";
        }
        JLabel startLabel = new JLabel(startLabelText + vertexTransport);
        startPanel.add(startLabel, BorderLayout.NORTH);

        JPanel finishPanel = new JPanel();
        finishPanel.setLayout(new BorderLayout());
        finishPanel.setPreferredSize(new Dimension(240, 20));
        String finishLabelText = "";
        if (time != null) {
            finishLabelText = time.getSecond().toString() + " - ";
        }
        JLabel finishLabel = new JLabel(finishLabelText + vertexTransport2);
        finishPanel.add(finishLabel, BorderLayout.SOUTH);

        JPanel linePanel = new JPanel();
        Color color = Color.decode(LineColor.getColor(lineName));
        linePanel.setBackground(color);
        linePanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK));

        JLabel lineLabel = new JLabel(lineName);
        lineLabel.setForeground(Color.WHITE);
        linePanel.setPreferredSize(new Dimension(15, 40));

        linePanel.add(lineLabel);

        startEndPanel.add(startPanel);
        startEndPanel.add(finishPanel);
        sectionPanel.add(linePanel);
        sectionPanel.add(startEndPanel);

        return sectionPanel;
    }

    /**
     * Divides EdgeTransport into an array of line - station values
     *
     * @param shortestPath list to divide
     * @return array of line - station values
     */
    private List<CustomPair> getSegments(List<EdgeTransport> shortestPath) {
        String currentLine = shortestPath.get(0).getLineName();
        String lastStationOnSameLine = null;
        VertexTransport lastStation = null;
        ArrayList<String[]> segments = new ArrayList<>();
        List<CustomPair> edges = new ArrayList<>();
        for (int i = 0; i < shortestPath.size(); i++) {
            EdgeTransport edge = shortestPath.get(i);
            if (!edge.getLineName().equals("CHANGE")) {

                if (i == 0) {
                    segments.add(
                            new String[] {edge.getLineName(), edge.getStartingStation().getName()});
                    edges.add(new CustomPair(edge.getLineName(), edge.getStartingStation()));
                }

                if (!edge.getLineName().equals(currentLine)) {
                    segments.add(new String[] {currentLine, lastStationOnSameLine});
                    edges.add(new CustomPair(currentLine, lastStation));
                    segments.add(
                            new String[] {edge.getLineName(), edge.getStartingStation().getName()});
                    edges.add(new CustomPair(edge.getLineName(), edge.getStartingStation()));
                    currentLine = edge.getLineName();
                }
                lastStationOnSameLine = edge.getEndingStation().getName();
                lastStation = edge.getEndingStation();
            }
        }

        segments.add(new String[] {currentLine, lastStationOnSameLine});
        edges.add(new CustomPair(currentLine, lastStation));

        return edges;
    }

    /**
     * Updates view with new data
     *
     * @param graphView GraphData to set in view
     */
    public void updateView(GraphData graphView) {
        frame.getContentPane().remove(panel);
        panel = createPanel(graphView);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }
}

class CustomPair {
    private String first;
    private VertexTransport second;

    public CustomPair(String first, VertexTransport second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public VertexTransport getSecond() {
        return second;
    }
}
