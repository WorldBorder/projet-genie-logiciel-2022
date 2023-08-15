package hubertmap.view;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import hubertmap.model.transport.EdgeTransport;
import hubertmap.model.transport.Line;
import hubertmap.model.transport.VertexTransport;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Specific class to give access only to the necessary data from the graph, without having direct
 * access.
 */
public class GraphData {
    Graph<VertexTransport, EdgeTransport> graph;
    private double minimumLongitude;
    private double maximumLatitude;
    private double minimumLatitude;
    private double maximumLongitude;
    private HashMap<String, Line> lines;

    /**
     * Constructs a new graphView instance and initializes its components. Calculates maximum and
     * minimum longitude and latitude.
     *
     * @param graph the graph that will be used in View
     * @param lines the lines that will be used in View
     */
    public GraphData(Graph<VertexTransport, EdgeTransport> graph, Set<Line> lines) {
        this.graph = graph;
        this.lines = new HashMap<>();
        for (Line line : lines) {
            this.lines.put(line.getName(), line);
        }

        minimumLongitude = 180;
        maximumLongitude = -180;
        minimumLatitude = 90;
        maximumLatitude = -90;

        for (VertexTransport station : graph.getVertices()) {
            if (station.getX() < minimumLongitude) minimumLongitude = station.getX();
            if (station.getX() > maximumLongitude) maximumLongitude = station.getX();
            if (station.getY() < minimumLatitude) minimumLatitude = station.getY();
            if (station.getY() > maximumLatitude) maximumLatitude = station.getY();
        }
    }

    /**
     * Returns Collection of vertices of graph
     *
     * @return Collection of vertices of graph
     */
    public Collection<VertexTransport> getVertices() {
        return graph.getVertices();
    }

    /**
     * Returns minimum longitude of all vertices
     *
     * @return minimum longitude of all vertices
     */
    public double getMinimumLongitude() {
        return minimumLongitude;
    }

    /**
     * Returns maximum longitude of all vertices
     *
     * @return maximum longitude of all vertices
     */
    public double getMaximumLongitude() {
        return maximumLongitude;
    }

    /**
     * Returns minimum latitude of all vertices
     *
     * @return minimum latitude of all vertices
     */
    public double getMinimumLatitude() {
        return minimumLatitude;
    }

    /**
     * Returns maximum latitude of all vertices
     *
     * @return maximum latitude of all vertices
     */
    public double getMaximumLatitude() {
        return maximumLatitude;
    }

    /**
     * Creates layout for for the specified graph.
     *
     * @return CircleLayout for the specified graph.
     */
    public Layout<VertexTransport, EdgeTransport> createLayout() {
        return new CircleLayout<VertexTransport, EdgeTransport>(graph);
    }

    /**
     * Returns the lines of the graph
     *
     * @return the lines of the graph
     */
    public HashMap<String, Line> getLines() {
        return lines;
    }
}
