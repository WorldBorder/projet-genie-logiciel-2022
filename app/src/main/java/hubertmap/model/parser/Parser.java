package hubertmap.model.parser;

import hubertmap.model.DurationJourney;
import hubertmap.model.Time;
import hubertmap.model.transport.EdgeTransport;
import hubertmap.model.transport.Line;
import hubertmap.model.transport.Network;
import hubertmap.model.transport.Station;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** The Parser class parses the input CSV files */
public class Parser {

    Network network;
    /** The list of all stations in the database. */
    private List<Station> stations = new ArrayList<>();
    /** The list of all lines in the database with their starting times. */
    private Map<Line, ArrayList<DurationJourney>> dataLine = new HashMap<>();

    /**
     * The constructor of the Parser class. It calls the parseStations() and parseLines() methods to
     * parse stations and lines from the CSV files. If the file is not found, it catches the
     * FileNotFoundException and prints an error message. If any other exception occurs, it catches
     * the Exception and prints an error message.
     *
     * @param parseAtCreation if we want to parse files at instanciation
     */
    public Parser(boolean parseAtCreation) {
        if (parseAtCreation) {
            try {
                parseStations(openFile("ressource/map_data.csv"));
                parseLines(openFile("ressource/timetables.csv"));
            } catch (FileNotFoundException e) {
                System.out.println("Le fichier n'a pas été trouvé : " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    Network getNetwork() {
        return network;
    }

    private void setNetwork(Network network) {
        this.network = network;
    }

    List<Station> getStations() {
        return stations;
    }

    /**
     * Returns the network edges.
     *
     * @return the network edges.
     */
    public Network getEdges() {
        return getNetwork();
    }

    /**
     * Opens the file at the given path and returns a File object. If the file is not found, it
     * catches the Exception and prints an error message.
     *
     * @param path the path of the file to open.
     * @return a File object of the file at the given path.
     */
    private File openFile(String path) {
        try {
            File file = new File(path);
            return file;
        } catch (Exception e) {
            System.out.println("Le fichier n'a pas été trouvé : " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if a line with the given name already exists in the database. If it does, it returns
     * the line object. If it doesn't, it throws an Exception with an error message.
     *
     * @param name the name of the line to check.
     * @return the line object if it already exists.
     * @throws Exception if the line doesn't already exist in the database.
     */
    private Line lineAlreadyExist(String name) throws Exception {
        for (Map.Entry<Line, ArrayList<DurationJourney>> entry : dataLine.entrySet()) {
            if (entry.getKey().getName().equals(name)) {
                return entry.getKey();
            }
        }
        throw new Exception("Line doesn't already exist in database");
    }

    /**
     * Creates a new Station object if it does not already exist for the first part of the line
     * name, or adds a new line to an existing Station.
     *
     * @param stationName the name of the station.
     * @param lineName the name of the line.
     * @param lat the latitude of the station.
     * @param lon the longitude of the station.
     * @return the Station object created or updated.
     */
    private Station createStation(String stationName, String lineName, float lat, float lon) {
        String simplelineName = lineName.split(" ")[0];
        Object[] sameStations =
                getStations().stream()
                        .filter(station -> station.getName().equals(stationName))
                        .toArray();

        if (sameStations.length > 0) {
            Station s = (Station) (sameStations[0]);
            lat = s.getX();
            lon = s.getY();
        }

        for (Object obj : sameStations) {
            Station st = (Station) obj;
            if (st.getSimpleLineName().equals(simplelineName)) {
                st.addLine(lineName);
                return st;
            }
        }

        Station newStation = new Station(stationName, lineName, lat, lon);

        for (Object obj : sameStations) {
            Station st = (Station) obj;
            st.setMultiLine(true);
            newStation.setMultiLine(true);
            EdgeTransport e =
                    new EdgeTransport(newStation, st, new DurationJourney(2 * 60), 5, "CHANGE");
            getNetwork().addEdge(e, newStation, st);
        }

        getStations().add(newStation);
        return newStation;
    }

    /**
     * Parses a file containing information about stations, lines and their connections, and creates
     * a network graph representation of this data. This method reads a CSV file, with each line
     * containing information about a connection between two stations.
     *
     * @param file the CSV file containing the stations and connections information
     * @throws Exception if there is an error reading or parsing the file
     */
    void parseStations(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String line;
        setNetwork(new Network());
        ArrayList<DurationJourney> durationJourneys = new ArrayList<>();
        String lastLineName = null;
        Station lastStation = null;
        Line currentLine = null;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(";");

            String station1Name = values[0].trim();
            float station1Lat = Float.parseFloat(values[1].trim().split(",")[0]);
            float station1Lon = Float.parseFloat(values[1].trim().split(",")[1]);
            String station2Name = values[2].trim();
            float station2Lat = Float.parseFloat(values[3].trim().split(",")[0]);
            float station2Lon = Float.parseFloat(values[3].trim().split(",")[1]);
            String lineName = values[4].trim();
            String timeString = values[5].trim();
            DurationJourney time =
                    new DurationJourney(timeString.split(":")[0], timeString.split(":")[1]);
            durationJourneys.add(time);

            float distance = Float.parseFloat(values[6].trim());
            Station station1;
            Station station2;

            station1 = createStation(station1Name, lineName, station1Lat, station1Lon);
            station2 = createStation(station2Name, lineName, station2Lat, station2Lon);

            if (lastStation == null) {
                lastStation = station2;
            }
            if (lastLineName == null) {
                lastLineName = lineName;
                currentLine = new Line(lineName, station1);
                dataLine.put(currentLine, durationJourneys);
            }

            boolean isNewLine = !lastLineName.equals(lineName);
            if (isNewLine) {
                currentLine.setTerminalStationArrival(lastStation);
                currentLine = new Line(lineName, station1);
                durationJourneys.remove(durationJourneys.size() - 1);
                durationJourneys = new ArrayList<>();
                dataLine.put(currentLine, durationJourneys);
                lastLineName = lineName;
                durationJourneys.add(time);
            }

            if (!station1.equals(station2)) {
                currentLine.addStationsIfNotAlreadyExist(station1);
                currentLine.addStationsIfNotAlreadyExist(station2);

                EdgeTransport edge =
                        new EdgeTransport(station1, station2, time, distance, lineName);
                getNetwork().addEdge(edge, station1, station2);
                lastStation = station2;
            }
        }
        reader.close();
        currentLine.setTerminalStationArrival(lastStation);
        getNetwork().setDataLine(dataLine);
    }

    /**
     * Parses a file containing information about the lines and their schedules, and fills in the
     * schedule information for each station on each line. This method reads a CSV file, with each
     * line containing information about a line's schedule.
     *
     * @param file the CSV file containing the lines and schedules information
     * @throws Exception if there is an error reading or parsing the file, or if the data given
     *     doesn't match
     */
    void parseLines(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        try (BufferedReader reader = new BufferedReader(isr)) {
            String csvLine;

            while ((csvLine = reader.readLine()) != null) {
                String[] values = csvLine.split(";");
                String terminus = values[1];

                String completeNameLine = values[0] + " variant " + values[3];
                Time start =
                        new Time(
                                Integer.parseInt(values[2].split(":")[0]),
                                Integer.parseInt(values[2].split(":")[1]),
                                0);

                Line currentLine = lineAlreadyExist(completeNameLine);
                if (!terminus.equals(currentLine.getTerminalStationDeparture().getName())) {
                    throw new Exception(
                            "Data given doesn't match\nThis line had "
                                    + currentLine.getTerminalStationDeparture().getName()
                                    + " as terminus start. The file has given "
                                    + terminus
                                    + " as terminus start station");
                } else {
                    currentLine.addStart(start);
                }
            }
            reader.close();
        }
        this.fillStationsSchedulesFromTerminusLineStart();
    }

    /**
     * Fills in the schedule information for each station on each line, based on the start times and
     * duration journeys between stations specified in the dataLine map.
     */
    private void fillStationsSchedulesFromTerminusLineStart() {
        Time timeToFillStationsSchedules = null;
        int i = 0;
        for (Line line : dataLine.keySet()) {
            for (Time time : line.starts) {
                timeToFillStationsSchedules = time;
                i = 0;
                for (DurationJourney dj : dataLine.get(line)) {
                    line.allStations
                            .get(i)
                            .addSchedule(line, new Time(timeToFillStationsSchedules));
                    timeToFillStationsSchedules =
                            timeToFillStationsSchedules.increaseWithADurationJourney(dj);
                    i++;
                }
                line.allStations.get(i).addSchedule(line, new Time(timeToFillStationsSchedules));
            }
        }
    }

    Map<Line, ArrayList<DurationJourney>> getDataLine() {
        return dataLine;
    }
}
