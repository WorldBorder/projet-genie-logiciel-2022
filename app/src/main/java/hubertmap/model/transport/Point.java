package hubertmap.model.transport;

/**
 * This class represents a Point used in walking journeys. A station has a name, coordinates (x and
 * y).
 */
public class Point extends VertexTransport {

    /**
     * Constructs a new Point with the given name and coordinates.
     *
     * @param name the name of the point
     * @param x the x-coordinate of the point's location
     * @param y the y-coordinate of the point's location
     */
    public Point(String name, double x, double y) {
        super(name, Float.valueOf(String.valueOf(x)), Float.valueOf(String.valueOf(y)));
    }
}
