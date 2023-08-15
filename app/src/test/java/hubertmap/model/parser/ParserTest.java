package hubertmap.model.parser;

import static org.junit.jupiter.api.Assertions.*;

import hubertmap.model.DurationJourney;
import hubertmap.model.transport.EdgeTransport;
import hubertmap.model.transport.Line;
import hubertmap.model.transport.Station;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Parser file can parser two types of files The second parser uses the data returned by the first
 * to add data to lines, stations and edges So it easier to iterate on small data sets to fully
 * recreate the environment needed by the second parser
 */
class ParserTest {

    private Parser parser;

    // returns a copy of the input string with UTF8 encoding
    String utf8String(String input) {
        return new String(input.getBytes(), StandardCharsets.UTF_8);
    }

    @BeforeEach
    public void setup() {
        parser = new Parser(false);
    }

    @AfterEach
    public void tearDown() {
        parser = null;
    }
    /**
     * For the parser test we will use a file containing a small part of the data on which the
     * information can be checked easily
     */
    @Test
    void parseStationsRetrieveCorrectAllStations() {
        try {
            parser.parseStations(
                    new File("src/test/java/hubertmap/model/parser/resources/map_data_test1.csv"));
        } catch (Exception e) {
        }
        List<Station> stations = parser.getStations();
        // Check all the stations from CSV are well stack in the stations arrayList
        String stationsFromCSV =
                utf8String(
                        "[Lourmel, Boucicaut, Félix Faure, Commerce, La Motte-Picquet - Grenelle,"
                            + " École Militaire, La Tour-Maubourg, Invalides, Concorde, Madeleine,"
                            + " Opéra, Richelieu - Drouot, Grands Boulevards, Bonne Nouvelle,"
                            + " Strasbourg - Saint-Denis, République, Filles du Calvaire,"
                            + " Saint-Sébastien - Froissart, Chemin Vert, Bastille, Ledru-Rollin,"
                            + " Faidherbe - Chaligny, Reuilly - Diderot, Montgallet, Daumesnil,"
                            + " Michel Bizot, Porte Dorée, Porte de Charenton, Liberté, Charenton -"
                            + " Écoles, Ecole Vétérinaire de Maisons-Alfort, Maisons-Alfort -"
                            + " Stade, Maisons-Alfort - Les Juilliottes, Créteil - L'Échat, Créteil"
                            + " - Université, Créteil - Préfecture, Pointe du Lac, Balard]");
        assertEquals(stations.toString(), stationsFromCSV);
    }

    @Test
    void parseStationsInsertCorrectDataLineInStation() {
        try {
            parser.parseStations(
                    new File("src/test/java/hubertmap/model/parser/resources/map_data_test1.csv"));
        } catch (Exception e) {
        }
        List<Station> stations = parser.getStations();

        // Lourmel is in both the first and the second variant and is the first station of the csv
        // file, and a start-of-line station

        Station lourmelStation =
                new Station("Lourmel", "8 variant 1", (float) 2.282242, (float) 48.83866);
        lourmelStation.addLine("8 variant 2");

        assertEquals(stations.get(0).getName(), lourmelStation.getName());
        assertEquals(stations.get(0).getAllLines(), lourmelStation.getAllLines());
        assertEquals(stations.get(0).getX(), lourmelStation.getX());
        assertEquals(stations.get(0).getY(), lourmelStation.getY());

        // Boucicaut is an intermediate station in both variant

        Station boucicautStation =
                new Station("Boucicaut", "8 variant 1", (float) 2.2879183, (float) 48.841022);
        boucicautStation.addLine("8 variant 2");

        assertEquals(stations.get(1).getName(), boucicautStation.getName());
        assertEquals(stations.get(1).getAllLines(), boucicautStation.getAllLines());
        assertEquals(stations.get(1).getX(), boucicautStation.getX());
        assertEquals(stations.get(1).getY(), boucicautStation.getY());

        // Pointe du Lac is an end-of-line station for both variants of line 8
        Station pointeDuLacStation =
                new Station("Pointe du Lac", "8 variant 1", (float) 2.4642901, (float) 48.7687522);
        pointeDuLacStation.addLine("8 variant 2");

        assertEquals(stations.get(stations.size() - 2).getName(), pointeDuLacStation.getName());
        assertEquals(
                stations.get(stations.size() - 2).getAllLines(), pointeDuLacStation.getAllLines());
        assertEquals(stations.get(stations.size() - 2).getX(), pointeDuLacStation.getX());
        assertEquals(stations.get(stations.size() - 2).getY(), pointeDuLacStation.getY());
    }

    @Test
    void parseStationsCheckEdgeData() {
        try {
            parser.parseStations(
                    new File("src/test/java/hubertmap/model/parser/resources/map_data_test1.csv"));
        } catch (Exception e) {
        }
        List<EdgeTransport> edges = new ArrayList<>(parser.getNetwork().getGraph().getEdges());
        String lourmelBoucicautEdge =
                utf8String(
                        "Lourmel - Boucicaut; durationJourney : (minutes : 0, secondes : 41);"
                                + " distance : 15.939358; line : 8");

        String boucicautFelixFaureEdge =
                utf8String(
                        "Boucicaut - Félix Faure; durationJourney : (minutes :"
                                + " 0, secondes : 25); distance : 11.195691; line : 8");

        assertTrue(edges.toString().contains(lourmelBoucicautEdge));
        assertTrue(edges.toString().contains(boucicautFelixFaureEdge));

        String creteilPrefecturePointeDuLac =
                utf8String(
                        "Créteil - Préfecture - Pointe du Lac; durationJourney : (minutes : 2,"
                                + " secondes : 23); distance : 70.15347; line : 8");

        assertTrue(edges.toString().contains(creteilPrefecturePointeDuLac));
    }

    @Test
    void parseLinesCheckTimeAndDurationJourney() {
        // Here as parseLines usually uses data retrieved from parseStations we need to insert faked
        // data to parser because we have to prevent dependency between the tests
        Station lourmelStation = new Station("Lourmel", "8 variant 1", null, null);
        parser.getStations().add(lourmelStation);
        parser.getStations().add(new Station("Boucicaut", "8 variant 1", null, null));
        parser.getStations().add(new Station(utf8String("Félix Faure"), "8 variant 1", null, null));

        ArrayList<DurationJourney> durationJourneys = new ArrayList<>();
        durationJourneys.add(new DurationJourney("4", "14"));
        durationJourneys.add(new DurationJourney("2", "58"));
        durationJourneys.add(new DurationJourney("3", "18"));
        parser.getDataLine().put(new Line("8 variant 1", lourmelStation), durationJourneys);

        try {
            parser.parseLines(
                    new File("src/test/java/hubertmap/model/parser/resources/timetables1.csv"));
        } catch (Exception e) {
        }

        assertTrue(
                parser.getDataLine()
                        .keySet()
                        .toString()
                        .contains("[09:10:00, 17:32:00, 16:29:00, 10:17:00]"));

        assertTrue(
                parser.getDataLine()
                        .values()
                        .toString()
                        .contains(
                                "[(minutes : 0, secondes : 41), (minutes : 0, secondes : 25),"
                                        + " (minutes : 0, secondes : 31)]"));
    }
}
