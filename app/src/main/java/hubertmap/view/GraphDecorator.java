package hubertmap.view;

import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import hubertmap.controller.Controller;
import hubertmap.model.transport.EdgeTransport;
import hubertmap.model.transport.Station;
import hubertmap.model.transport.VertexTransport;
import java.awt.*;
import java.awt.BasicStroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections15.Transformer;

/**
 * The GraphDecorator class provides methods to decorate a graph with colors, strokes, sizes, and
 * labels for vertices and edges.
 */
public class GraphDecorator {

    /** The scale used to size the vertices of the graph. */
    private static int scale;

    /** The list of edges in the shortest path of the graph. */
    List<EdgeTransport> shortestPathEdges;

    /** The list of stations in the shortest path of the graph. */
    List<VertexTransport> shortestPathStations;

    /** Constructs a new GraphDecorator object with default settings. */
    public GraphDecorator() {
        scale = 0;
        shortestPathEdges = new ArrayList<>();
        shortestPathStations = new ArrayList<>();
    }

    /**
     * Returns a transformer that is used to set vertex colors.
     *
     * @return a Transformer object that maps a Station object to a Paint object representing its
     *     color.
     */
    public Transformer<VertexTransport, Paint> vertexColor() {
        Transformer<VertexTransport, Paint> vertexColor =
                new Transformer<VertexTransport, Paint>() {
                    public Paint transform(VertexTransport input) {
                        if (input instanceof Station) {
                            Station station = (Station) input;
                            if (station.isMultiLine()) return Color.WHITE;
                            else
                                return Color.decode(
                                        LineColor.getColor(station.getSimpleLineName()));
                        } else return Color.WHITE;
                    }
                };
        return vertexColor;
    }

    /**
     * Returns a transformer that used to set edges colors. Sets no color if edge doesn't have a
     * line name, unless it's part of the current shortest path
     *
     * @return a Transformer object that maps an EdgeTransport object to a Paint object representing
     *     its color.
     */
    public Transformer<EdgeTransport, Paint> edgeColor() {
        return (EdgeTransport input) -> {
            String lineName = input.getLineName();
            if ((!lineName.equals("CHANGE"))
                    && (lineName.length() > 0 || shortestPathEdges.contains(input))) {
                return Color.decode(LineColor.getColor(lineName));
            } else {
                return null;
            }
        };
    }

    /**
     * Returns a transformer that used to set edges strokes. Sets specific stroke if edge is in
     * shortestPath.
     *
     * @return a Transformer object that maps an EdgeTransport object to a Stroke object
     *     representing its stroke.
     */
    public Transformer<EdgeTransport, Stroke> edgeStroke() {
        return new Transformer<EdgeTransport, Stroke>() {
            @Override
            public Stroke transform(EdgeTransport input) {
                if (shortestPathEdges.contains(input)) return new BasicStroke(6f);
                else return new BasicStroke(1.5f);
            }
        };
    }

    /**
     * Creates a new graph mouse for the graph. Update scale on moves of mouse wheel
     *
     * @return a DefaultModalGraphMouse object for the graph.
     */
    public DefaultModalGraphMouse<Station, EdgeTransport> createGraphMouse() {
        DefaultModalGraphMouse<Station, EdgeTransport> graphMouse =
                new DefaultModalGraphMouse<Station, EdgeTransport>() {
                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {
                        super.mouseWheelMoved(e);
                        scale += e.getWheelRotation();
                    }
                };
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        return graphMouse;
    }

    /**
     * Returns a transformer that used to set vertex sizes. Dynamically changes the size depending
     * on the scale
     *
     * @return a Transformer object that maps a Station object to a Shape object representing its
     *     size.
     */
    public Transformer<VertexTransport, Shape> vertexSize() {
        Transformer<VertexTransport, Shape> vertexSize =
                new Transformer<VertexTransport, Shape>() {
                    @Override
                    public Shape transform(VertexTransport input) {
                        if (shortestPathStations.contains(input))
                            return new Ellipse2D.Float(-5, -5, 10, 10);
                        else {
                            if (scale > 15) return new Ellipse2D.Float(-5, -5, 10, 10);
                            else {
                                int value = scale / 3;
                                return new Ellipse2D.Float(-value, -value, value * 2, value * 2);
                            }
                        }
                    }
                };
        return vertexSize;
    }

    /**
     * Returns a labeler that used to set vertex labels.
     *
     * @return a ToStringLabeller object that maps a Station object to a String object representing
     *     its label.
     */
    public ToStringLabeller<VertexTransport> toStringLabeller() {
        ToStringLabeller<VertexTransport> labeller =
                new ToStringLabeller<>() {
                    @Override
                    public String transform(VertexTransport v) {
                        if (shortestPathStations.contains(v)) return super.transform(v);
                        else {
                            if (scale < 20) return "";
                            else return super.transform(v);
                        }
                    }
                    ;
                };
        return labeller;
    }

    /**
     * Sets the shortest path of the graph.
     *
     * @param shortestPath the list of edges in the shortest path of the graph.
     */
    public void setShortestPath(List<EdgeTransport> shortestPath) {
        if (shortestPath != null) {
            this.shortestPathEdges = shortestPath;
            shortestPathStations.clear();
            for (EdgeTransport edgeTransport : shortestPath) {
                VertexTransport station1 = edgeTransport.getStartingStation();
                VertexTransport station2 = edgeTransport.getEndingStation();
                shortestPathStations.add(station1);
                shortestPathStations.add(station2);
            }
            Set<VertexTransport> set = new HashSet<>(shortestPathStations);
            shortestPathStations.clear();
            shortestPathStations.addAll(set);
        }
    }

    /**
     * Returns a new GraphMouseListener for Stations, which listens for clicks on the graph, and
     * calls the setSchedules method of the Controller class with the clicked Station.
     *
     * @return a new GraphMouseListener object for Stations.
     */
    public GraphMouseListener<VertexTransport> graphMouseListener() {
        return new GraphMouseListener<VertexTransport>() {

            @Override
            public void graphClicked(VertexTransport v, MouseEvent me) {
                // TODO Auto-generated method stub
                if (v instanceof Station) Controller.setSchedules((Station) v);
            }

            @Override
            public void graphPressed(VertexTransport v, MouseEvent me) {
                // TODO Auto-generated method stub

            }

            @Override
            public void graphReleased(VertexTransport v, MouseEvent me) {
                // TODO Auto-generated method stub

            }
        };
    }
}
