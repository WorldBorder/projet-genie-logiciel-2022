package hubertmap.model.transport;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import hubertmap.model.DurationJourney;
import hubertmap.model.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a network of vertex and edges between them, forming a transport network.
 */
public class Network {
    private static Graph<VertexTransport, EdgeTransport> graph;
    private HashMap<String, VertexTransport> stations;
    private List<Point> userPoints;

    private DijkstraShortestPath<VertexTransport, EdgeTransport> distancePaths;
    private Map<Line, ArrayList<DurationJourney>> datatLine;

    private int pointCount = 0;

    /**
     * Constructs a new Network object with the given collection of edges.
     *
     * @param edges the collection of edges to add to the network
     */
    public Network(Collection<EdgeTransport> edges) {
        graph = new SparseGraph<>();
        stations = new HashMap<>();
        userPoints = new ArrayList<>();

        if (edges != null) {
            for (EdgeTransport e : edges) {
                addEdge(e);
            }
        }

        distancePaths = new DijkstraShortestPath<>(graph, EdgeTransport::estimateWeight);
    }

    /** Constructs a new Network object with no edges. */
    public Network() {
        this(null);
    }

    /**
     * Adds an edge to the network, connecting the given stations.
     *
     * @param edge the edge to add
     * @param vertexTransport the first station to connect
     * @param vertexTransport2 the second station to connect
     */
    public void addEdge(
            EdgeTransport edge, VertexTransport vertexTransport, VertexTransport vertexTransport2) {
        graph.addEdge(edge, vertexTransport, vertexTransport2);

        stations.putIfAbsent(vertexTransport.getName().toLowerCase(), vertexTransport);
        stations.putIfAbsent(vertexTransport2.getName().toLowerCase(), vertexTransport2);
    }

    /**
     * Adds an edge to the network, connecting the stations at the start and end of the edge.
     *
     * @param edge the edge to add
     */
    public void addEdge(EdgeTransport edge) {
        addEdge(edge, edge.getStartingStation(), edge.getEndingStation());
    }

    /**
     * Returns the graph representing the network.
     *
     * @return the graph representing the network
     */
    public Graph<VertexTransport, EdgeTransport> getGraph() {
        return graph;
    }

    /**
     * Calculates the shortest path from station1 to station2 using the distance between two
     * stations as weight
     *
     * <p>for each edge returned, swaps starting and ending stations if needed, corresponding with
     * the direction taken
     *
     * @param station1 starting station
     * @param station2 destination station
     * @return a list edges to visit in the correct order
     */
    public List<EdgeTransport> shortestPath(VertexTransport station1, VertexTransport station2) {
        List<EdgeTransport> list = distancePaths.getPath(station1, station2);
        VertexTransport s = station1;
        for (EdgeTransport e : list) {
            if (!e.getStartingStation().equals(s)) {
                e.swapStations();
            }
            s = e.getEndingStation();
        }
        return list;
    }

    /**
     * Calculates the shortest path from station1 to station2 using the distance between two
     * stations as weight
     *
     * @param station1 name of the starting station
     * @param station2 name of the destination station
     * @return a list edges to visit in the correct order
     */
    public List<EdgeTransport> shortestPath(String station1, String station2) {
        if (stations.get(station1) != null && stations.get(station2) != null) {
            List<EdgeTransport> path = shortestPath(stations.get(station1), stations.get(station2));
            // omits line changes at the extremities
            if (!path.isEmpty() && path.get(0).getLineName().equals("CHANGE")) {
                path.remove(0);
            }
            if (!path.isEmpty() && path.get(path.size() - 1).getLineName().equals("CHANGE")) {
                path.remove(path.size() - 1);
            }
            return path;
        } else return null;
    }

    /**
     * Set the list of lines in the network with their duration.
     *
     * @param dataLine the list of lines with their duration
     */
    public void setDataLine(Map<Line, ArrayList<DurationJourney>> dataLine) {
        this.datatLine = dataLine;
    }

    /**
     * Returns the list of lines in the network.
     *
     * @return the list of lines in the network
     */
    public Set<Line> getLines() {
        return datatLine.keySet();
    }

    /**
     * Creates a point defined by the coordinates provided by the user
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return name of created point
     */
    public String createPoint(double x, double y) {
        Point p = new Point("point" + pointCount, x, y);
        pointCount++;
        ArrayList<EdgeTransport> newEdges = new ArrayList<>();
        for (VertexTransport v : graph.getVertices()) {
            float distance = calculateDistance(p.getX(), p.getY(), v.getX(), v.getY());
            DurationJourney durationJourney = calculateDurationJourney(distance);
            newEdges.add(new EdgeTransport((VertexTransport) p, v, durationJourney, distance, ""));
        }

        for (EdgeTransport edgeTransport : newEdges) {
            addEdge(edgeTransport);
        }

        userPoints.add(p);

        return p.getName();
    }

    /**
     * Calculates the distance between two points on Earth using the Haversine formula.
     *
     * @param x1 the latitude of the first point in degrees
     * @param y1 the longitude of the first point in degrees
     * @param x2 the latitude of the second point in degrees
     * @param y2 the longitude of the second point in degrees
     * @return the distance between the two points in 10th of km
     */
    private float calculateDistance(float x1, float y1, float x2, float y2) {
        float earthRadius = 6371.0f; // Earth's radius in kilometers
        float dLat = (float) Math.toRadians(x2 - x1);
        float dLon = (float) Math.toRadians(y2 - y1);
        float lat1 = (float) Math.toRadians(x1);
        float lat2 = (float) Math.toRadians(x2);

        float a =
                (float)
                        (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                                + Math.sin(dLon / 2)
                                        * Math.sin(dLon / 2)
                                        * Math.cos(lat1)
                                        * Math.cos(lat2));
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        float distance = earthRadius * c;

        return distance * 10; // distance is in 10th of km
    }

    /**
     * Calculates the duration of a journey based on the given distance and a walking speed of 5
     * km/h.
     *
     * @param distance the distance of the journey in 10th of km
     * @return the duration of the journey as a {@link DurationJourney} object
     */
    private DurationJourney calculateDurationJourney(float distance) {
        distance /= 10; // putting distance back in km
        int walkingSpeed = 5; // km/h
        int walkingTimeInSeconds = (int) Math.round((distance / walkingSpeed) * 3600);
        return new DurationJourney(walkingTimeInSeconds);
    }

    /** Deletes all user points from the graph. */
    public void deleteUserPoints() {
        for (Point point : userPoints) {
            graph.removeVertex(point);
            stations.values().remove(point);
        }
    }

    /**
     * Gets the times of departure and arrival for a given path between two stations or a station
     * and a point.
     *
     * @param shortestPath The list of edges that represents the shortest path.
     * @param vertexTransport1 The name of the first vetrex.
     * @param vertexTransport2 The name of the second vetrex.
     * @param currentTime The current time at which the user wants to travel.
     * @return A pair of times, the first representing the departure time and the second the arrival
     *     time. If there is no possible path, it returns null.
     */
    public Pair<Time> getTimes(
            List<EdgeTransport> shortestPath,
            VertexTransport vertexTransport1,
            VertexTransport vertexTransport2,
            Time currentTime) {

        // If the vetrexes are equal, return the transfer time
        if (vertexTransport1.equals(vertexTransport2)) {
            Time arrivalTime = new Time(currentTime);
            arrivalTime.increaseByMinute(2);
            return new Pair<Time>(currentTime, arrivalTime);
        }
        // Check if both vetrexes are stations
        else if (vertexTransport1 instanceof Station && vertexTransport2 instanceof Station) {
            Station station1 = (Station) vertexTransport1;
            Station station2 = (Station) vertexTransport2;

            // Get common variants for both stations
            Set<String> commonLines = new HashSet<>(station1.getSchedules().keySet());
            commonLines.retainAll(station2.getSchedules().keySet());

            // If there are common options, find the time of departure
            if (commonLines.size() > 0) {
                Time departTime = new Time(0, 0, 0);
                for (String lineName : commonLines) {
                    // Check if the departure station is after the arrival station (stations stored
                    // in lines should be sorted)
                    if (endAfterStart(lineName, station1, station2)) {
                        // Find the nearest departure time and the variant to which it belongs
                        Time time = nextDepart(lineName, station1, currentTime);
                        if (time != null && time.compareTo(departTime) >= 0) {
                            departTime = time;
                        }
                    }
                }

                // To find the arrival time, add to the departure time the travel time and 2 minutes
                // for the transfer
                Time arrivalTime = null;
                if (!departTime.equals(new Time(0, 0, 0))) {
                    arrivalTime = calculatePathTime(departTime, shortestPath, station1, station2);
                    if (arrivalTime != null) {
                        return new Pair<Time>(departTime, arrivalTime);
                    }
                }
            }
        }
        // One of the vertices is a point, return the walking time
        else {
            Time departTime = currentTime;
            if (vertexTransport1 != null && vertexTransport2 != null) {
                Time arrivalTime =
                        calculatePathTime(
                                departTime, shortestPath, vertexTransport1, vertexTransport2);
                return new Pair<Time>(departTime, arrivalTime);
            } else return null;
        }

        return null;
    }

    /**
     * Calculates the total time of a path given the departure time, the path and the two vertices
     * of the path.
     *
     * @param departTime The departure time from the first vertex.
     * @param shortestPath The list of edges that represents the path.
     * @param vertexTransport The starting vertex of the path.
     * @param vertexTransport2 The ending vertex of the path.
     * @return The arrival time at the end of the path. If there is no possible path, it returns
     *     null.
     */
    private Time calculatePathTime(
            Time departTime,
            List<EdgeTransport> shortestPath,
            VertexTransport vertexTransport,
            VertexTransport vertexTransport2) {
        Time time = new Time(departTime);
        VertexTransport currentStation = vertexTransport;
        for (EdgeTransport edgeTransport : shortestPath) {
            if (edgeTransport.getStartingStation().equals(currentStation)) {
                currentStation = edgeTransport.getEndingStation();
                // we add 15 seconds to take stops into account
                DurationJourney d = new DurationJourney(15).add(edgeTransport.getDurationJourney());
                time = time.increaseWithADurationJourney(d);
            }

            if (edgeTransport.getEndingStation().equals(vertexTransport2)) {
                return time;
            }
        }
        return null;
    }

    /**
     * Returns the next departure time for a given line at a given station.
     *
     * @param lineName The name of the line.
     * @param station1 The station where the user wants to take the line.
     * @param currentTime The current time at which the user wants to travel.
     * @return The next departure time after the current time. If there is no possible departure
     *     time, it returns null.
     */
    private Time nextDepart(String lineName, Station station1, Time currentTime) {
        Set<Time> stationTimes = station1.getSchedules().get(lineName);
        if (stationTimes != null) {
            for (Time time : stationTimes) {
                String timeString = time.toString();
                LocalTime stationTime = LocalTime.parse(timeString);
                LocalTime timeToCheck = LocalTime.parse(currentTime.toString());
                if (stationTime.isAfter(timeToCheck)) {
                    return time;
                }
            }
        }
        return null;
    }

    /**
     * Checks if the second station is after the first station on a given line.
     *
     * @param lineName The name of the line.
     * @param station1 The first station.
     * @param station2 The second station.
     * @return True if the second station is after the first station, false otherwise.
     */
    private boolean endAfterStart(String lineName, Station station1, Station station2) {
        Line currentLine = getLineByName(getLines(), lineName);
        if (currentLine != null) {
            ArrayList<Station> lineStations = currentLine.getAllStations();
            int index1 = lineStations.indexOf(station1);
            int index2 = lineStations.indexOf(station2);
            if (index1 < index2) return true;
            else return false;
        }
        return false;
    }

    /**
     * Returns the line with the given name.
     *
     * @param lines The set of lines to search.
     * @param name The name of the line.
     * @return The line with the given name. If there is no line with the given name, it returns
     *     null.
     */
    private Line getLineByName(Set<Line> lines, String name) {
        for (Line line : lines) {
            if (line.getName().equals(name)) {
                return line;
            }
        }
        return null;
    }
}
