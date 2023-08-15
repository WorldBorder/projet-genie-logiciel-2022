package hubertmap.model.transport;

import static org.junit.jupiter.api.Assertions.*;

import hubertmap.model.DurationJourney;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NetworkTest {

    @Test
    void shortestPathTest() {
        Station a = new Station("A", "", 0.0f, 0.0f);
        Station b = new Station("B", "", 0.0f, 0.0f);
        Station c = new Station("C", "", 0.0f, 0.0f);

        EdgeTransport ab = new EdgeTransport(a, b, new DurationJourney(10), 10.0f, "");
        EdgeTransport bc = new EdgeTransport(b, c, new DurationJourney(10), 10.0f, "");
        EdgeTransport ac = new EdgeTransport(a, c, new DurationJourney(50), 50.0f, "");

        ArrayList<EdgeTransport> edges = new ArrayList<>();
        edges.add(ab);
        edges.add(bc);
        edges.add(ac);

        Network g = new Network(edges);
        List<EdgeTransport> path = g.shortestPath(a, c);

        List<EdgeTransport> expectedPath = new ArrayList<EdgeTransport>();
        expectedPath.add(ab);
        expectedPath.add(bc);

        assertEquals(expectedPath, path);
    }

    @Test
    void noShortestPathTest() {
        Station a = new Station("A", "", 0.0f, 0.0f);
        Station b = new Station("B", "", 0.0f, 0.0f);
        Station c = new Station("C", "", 0.0f, 0.0f);
        Station d = new Station("D", "", 0.0f, 0.0f);
        ArrayList<EdgeTransport> edges = new ArrayList<>();
        edges.add(new EdgeTransport(a, b, new DurationJourney(1), 1.0f, ""));
        edges.add(new EdgeTransport(c, d, new DurationJourney(1), 1.0f, ""));
        Network g = new Network(edges);
        List<EdgeTransport> path = g.shortestPath(a, c);
        assertTrue(path.isEmpty());
    }

    @Test
    void trivialShortestPathTest() {
        Station a = new Station("A", "", 0.0f, 0.0f);
        Station b = new Station("B", "", 0.0f, 0.0f);
        ArrayList<EdgeTransport> edges = new ArrayList<>();
        edges.add(new EdgeTransport(a, b, new DurationJourney(1), 1.0f, ""));
        Network g = new Network(edges);
        List<EdgeTransport> path = g.shortestPath(a, a);
        assertTrue(path.isEmpty());
    }
}
