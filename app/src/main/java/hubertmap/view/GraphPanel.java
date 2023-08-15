package hubertmap.view;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import hubertmap.model.transport.EdgeTransport;
import hubertmap.model.transport.VertexTransport;
import java.awt.*;
import java.awt.geom.Point2D;
import javax.swing.*;

/**
 * The GraphPanel class constructs a new JPanel instance for graph visualization and initializes its
 * components.
 */
class GraphPanel extends JPanel {

    /** The width of the panel. */
    private int panelWidth;

    /** The height of the panel. */
    private int panelHeight;

    /** The layout used to arrange the stations and their edges. */
    Layout<VertexTransport, EdgeTransport> layout;

    /** The viewer used to display the graph. */
    VisualizationViewer<VertexTransport, EdgeTransport> vv;

    /** The view containing the graph data. */
    GraphData graphView;

    /** The decorator used to customize the appearance of the graph. */
    GraphDecorator decorator;

    /**
     * Constructs a new GraphPanel instance with the specified GraphView.
     *
     * @param graphView the GraphView instance to use
     */
    public GraphPanel(GraphData graphView) {

        this.graphView = graphView;

        panelWidth = 800;
        panelHeight = 800;

        layout = graphView.createLayout();
        layout.setSize(new Dimension(panelWidth, panelHeight));
        vv = new VisualizationViewer<VertexTransport, EdgeTransport>(layout);

        setUpCoords();

        decorator = new GraphDecorator();

        vv.setGraphMouse(decorator.createGraphMouse());
        vv.addGraphMouseListener(decorator.graphMouseListener());

        vv.getRenderContext().setVertexFillPaintTransformer(decorator.vertexColor());
        vv.getRenderContext().setVertexShapeTransformer(decorator.vertexSize());
        vv.getRenderContext().setVertexLabelTransformer(decorator.toStringLabeller());
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<>());
        vv.getRenderContext().setEdgeStrokeTransformer(decorator.edgeStroke());
        vv.getRenderContext().setEdgeDrawPaintTransformer(decorator.edgeColor());
        vv.setPreferredSize(new Dimension(panelWidth, panelHeight));

        this.add(vv);
    }

    /**
     * Sets up the coordinates for the stations based on the panel dimensions and the station's
     * location.
     */
    private void setUpCoords() {
        double minimumLongitude = graphView.getMinimumLongitude();
        double maximumLongitude = graphView.getMaximumLongitude();
        double minimumLatitude = graphView.getMinimumLatitude();
        double maximumLatitude = graphView.getMaximumLatitude();

        double longitudeDiff = maximumLongitude - minimumLongitude;
        double latitudeDiff = maximumLatitude - minimumLatitude;

        double longitudeScale = panelWidth / longitudeDiff;
        double latitudeScale = panelHeight / latitudeDiff;

        for (VertexTransport station : graphView.getVertices()) {
            int stationx = (int) Math.round((station.getX() - minimumLongitude) * longitudeScale);
            int stationy = (int) Math.round((maximumLatitude - station.getY()) * latitudeScale);
            layout.setLocation(station, new Point2D.Double(stationx, stationy));
        }
    }

    /**
     * Returns the GraphDecorator instance used by this GraphPanel.
     *
     * @return the GraphDecorator instance used by this GraphPanel
     */
    public GraphDecorator getDecorator() {
        return decorator;
    }
}
