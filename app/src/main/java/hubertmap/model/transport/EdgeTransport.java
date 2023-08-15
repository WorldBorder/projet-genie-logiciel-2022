package hubertmap.model.transport;

import hubertmap.model.DurationJourney;

/**
 * The EdgeTransport class represents a transport edge between two stations. It contains information
 * about the journey duration, distance, and the starting and ending stations.
 */
public class EdgeTransport {

    private DurationJourney durationJourney;
    private float distance;
    private VertexTransport startingStation;
    private VertexTransport endingStation;
    private String lineName;

    /**
     * Creates a new EdgeTransport object with the given starting and ending stations, duration, and
     * distance.
     *
     * @param startingStation the starting station of the transport edge
     * @param endingStation the ending station of the transport edge
     * @param durationJourney the duration of the journey between the starting and ending stations
     * @param distance the distance between the starting and ending stations
     * @param lineName name of the line of which the face is a part
     */
    public EdgeTransport(
            VertexTransport startingStation,
            VertexTransport endingStation,
            DurationJourney durationJourney,
            float distance,
            String lineName) {
        this.startingStation = startingStation;
        this.endingStation = endingStation;
        this.durationJourney = durationJourney;
        this.distance = distance;
        this.lineName = lineName.split(" ")[0]; // variant is not important
    }

    /**
     * Returns a string representation of the EdgeTransport object, including the starting and
     * ending stations, journey duration, and distance.
     *
     * @return a string representation of the EdgeTransport object
     */
    @Override
    public String toString() {
        return startingStation
                + " - "
                + endingStation
                + "; durationJourney : "
                + durationJourney
                + "; distance : "
                + distance
                + "; line : "
                + lineName;
    }

    /**
     * @return a copy of current object
     */
    public EdgeTransport copy() {
        return new EdgeTransport(
                startingStation, endingStation, durationJourney.copy(), distance, lineName);
    }

    /**
     * Returns the starting station of the transport edge.
     *
     * @return the starting station of the transport edge
     */
    public VertexTransport getStartingStation() {
        return startingStation;
    }
    /**
     * Returns the ending station of the transport edge.
     *
     * @return the ending station of the transport edge
     */
    public VertexTransport getEndingStation() {
        return endingStation;
    }
    /**
     * Returns the distance between the starting and ending stations.
     *
     * @return the distance between the starting and ending stations
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Returns the line name, which omits the variant
     *
     * @return line name
     */
    public String getLineName() {
        return lineName;
    }

    /**
     * Returns the durationJourney object corresponding to the duration of the journey
     *
     * @return durationJourney
     */
    public DurationJourney getDurationJourney() {
        return durationJourney;
    }

    /**
     * Returns an estimate of the weight used for shortest path calculations
     *
     * @return the weight of the edge
     */
    public float estimateWeight() {
        // we could add extra time or distance
        // when moving by foot
        return durationJourney.toSeconds() + 20.0f; // avg waiting at a station
    }

    /** swaps startingStation and endingStation */
    public void swapStations() {
        VertexTransport tmp = endingStation;
        endingStation = startingStation;
        startingStation = tmp;
    }
}
