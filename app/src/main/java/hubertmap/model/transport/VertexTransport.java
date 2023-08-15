package hubertmap.model.transport;

/**
 * This class represents a abstract Vertex used in a transportation system. A Vertex has a name and
 * coordinates (x and y).
 */
public abstract class VertexTransport {
    private String name;
    private Float x;
    private Float y;

    /**
     * Constructs a new Vertex with the given name and coordinates.
     *
     * @param name the name of the station
     * @param x the x-coordinate of the station's location
     * @param y the y-coordinate of the station's location
     */
    public VertexTransport(String name, Float x, Float y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the name of the vertex.
     *
     * @return the name of the vertex
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the new name of vertex
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the x-coordinate of the vertex's location.
     *
     * @return the x-coordinate of the vertex's location
     */
    public Float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the vertex's location.
     *
     * @return the y-coordinate of the vertex's location
     */
    public Float getY() {
        return y;
    }

    /**
     * Determines whether this vertex is equal to the specified vertex. Two vertexes are considered
     * equal if they have the same name and location.
     *
     * @param vertex the vertex to compare this vertex to
     * @return true if this vertex is equal to the specified vertex, false otherwise
     */
    public boolean equals(VertexTransport vertex) {
        return (this.name.equals(vertex.getName())
                && this.x.equals(vertex.getX())
                && this.y.equals(vertex.getY()));
    }

    /**
     * Returns the name of the vertex.
     *
     * @return the name of the vertex
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return name;
    }
}
