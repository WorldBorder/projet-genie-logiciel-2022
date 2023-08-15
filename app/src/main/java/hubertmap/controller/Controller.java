package hubertmap.controller;

import edu.uci.ics.jung.graph.util.Pair;
import hubertmap.model.Time;
import hubertmap.model.parser.Parser;
import hubertmap.model.transport.EdgeTransport;
import hubertmap.model.transport.Network;
import hubertmap.model.transport.Station;
import hubertmap.model.transport.VertexTransport;
import hubertmap.view.GraphData;
import hubertmap.view.View;
import java.time.LocalTime;
import java.util.List;

/**
 * The Controller class is responsible for managing the interaction between the Model and the View
 * in a Model-View-Controller (MVC) architecture. It handles updates to the model and the view, and
 * ensures that they stay in sync with each other.
 */
public class Controller {

    /* The network of stations and edges managed in Model */
    static Network network;

    /* The GUI of application. */
    static View view;

    static GraphData graphView;

    /** Constructs a new Controller instance, creates Network and View */
    public Controller() {
        Parser parser = new Parser(true);
        network = parser.getEdges();
        graphView = new GraphData(network.getGraph(), network.getLines());
        view = new View(graphView);
    }

    /**
     * Calculates the shortest path between two stations and updates the view accordingly.
     *
     * @param time start time of travel
     * @param station1Name the name of the starting station
     * @param station2Name the name of the destination station
     */
    public static void setShortestPath(LocalTime time, String station1Name, String station2Name) {
        var shortestPath = network.shortestPath(station1Name, station2Name);
        if (shortestPath != null) view.setShortestPath(time, shortestPath);
    }

    /**
     * Sets departure station to view
     *
     * @param name station name to set
     */
    public static void setDeparture(String name) {
        view.setDeparture(name);
    }

    /**
     * Sets arrival station to view
     *
     * @param name station name to set
     */
    public static void setArrival(String name) {
        view.setArrival(name);
    }

    /**
     * Sets schedules to view
     *
     * @param v station with schedules to set
     */
    public static void setSchedules(Station v) {
        view.setSchedules(v);
    }

    /**
     * Creates a point defined by the coordinates provided by the user and updates view
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return name of created point
     */
    public static String createPoint(double x, double y) {

        String pointName = network.createPoint(x, y);

        graphView = new GraphData(network.getGraph(), network.getLines());
        view.updateView(graphView);

        return pointName;
    }

    /** Deletes all user points from the graph. */
    public static void deleteUserPoints() {
        network.deleteUserPoints();
    }

    /**
     * Calculates the departure and arrival time for two vetrexes.
     *
     * @param shortestPath Shortest path.
     * @param vertexTransport1 The name of the starting vetrex.
     * @param vertexTransport2 The name of the ending vetrex.
     * @param currentTime The current time as a Time object.
     * @return A Pair object containing the departure time and the arrival time as Time objects, or
     *     null if the calculation failed.
     */
    public static Pair<Time> getTimes(
            List<EdgeTransport> shortestPath,
            VertexTransport vertexTransport1,
            VertexTransport vertexTransport2,
            Time currentTime) {
        return network.getTimes(shortestPath, vertexTransport1, vertexTransport2, currentTime);
    }
}
