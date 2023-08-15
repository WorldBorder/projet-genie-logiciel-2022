package hubertmap.model.transport;

import hubertmap.model.Time;
import java.util.ArrayList;

/** The Line class represents a transportation line that connects two terminal stations. */
public class Line {
    private String name;
    private Station terminalStationDeparture;
    private Station terminalStationArrival;
    /** The list of all starting times of the line. */
    public ArrayList<Time> starts;
    /** The list of all stations that the line passes through. */
    public ArrayList<Station> allStations;

    /**
     * Constructs a new instance of the Line class with a specified name, departure terminal
     * station, arrival terminal station and starting time.
     *
     * @param name The name of the metro line.
     * @param terminalStationDeparture The departure terminal station of the metro line.
     * @param terminalStationArrival The arrival terminal station of the metro line.
     * @param start The starting time of the metro line.
     */
    public Line(
            String name,
            Station terminalStationDeparture,
            Station terminalStationArrival,
            Time start) {
        this.name = name;
        this.terminalStationDeparture = terminalStationDeparture;
        this.terminalStationArrival = terminalStationArrival;
        this.starts = new ArrayList<>();
        this.allStations = new ArrayList<>();
        this.starts.add(start);
    }

    /**
     * Constructs a new instance of the Line class with a specified name and departure terminal
     * station.
     *
     * @param name The name of the metro line.
     * @param terminaleStationDeparture The departure terminal station of the metro line.
     */
    public Line(String name, Station terminaleStationDeparture) {
        this.name = name;
        this.terminalStationDeparture = terminaleStationDeparture;
        terminalStationArrival = null;
        this.starts = new ArrayList<>();
        this.allStations = new ArrayList<>();
    }

    /**
     * Returns the name of the metro line.
     *
     * @return The name of the metro line.
     */
    public String getName() {
        return name;
    }
    /**
     * Returns the departure terminal station of the metro line.
     *
     * @return The departure terminal station of the metro line.
     */
    public Station getTerminalStationDeparture() {
        return terminalStationDeparture;
    }

    /**
     * Returns the arrival terminal station of the metro line.
     *
     * @return The arrival terminal station of the metro line.
     */
    public Station getTerminalStationArrival() {
        return terminalStationArrival;
    }

    /**
     * Adds a new station to the metro line if it doesn't already exist in the list of stations.
     *
     * @param newStation The new station to be added to the metro line.
     */
    public void addStationsIfNotAlreadyExist(Station newStation) {
        for (Station station : allStations) {
            if (station.getName().equals(newStation.getName())) {
                return;
            }
        }
        allStations.add(newStation);
    }

    /**
     * Sets the arrival terminal station of the metro line.
     *
     * @param terminalStationArrival The arrival terminal station of the metro line.
     */
    public void setTerminalStationArrival(Station terminalStationArrival) {
        this.terminalStationArrival = terminalStationArrival;
    }

    /**
     * Adds a new starting time to the metro line.
     *
     * @param start The new starting time to be added to the metro line.
     */
    public void addStart(Time start) {
        starts.add(start);
    }
    /**
     * Returns a string representation of this line in the format: name, Starting Station :
     * terminalStationDeparture, Terminus : terminalStationArrival, Time : starts
     *
     * @return a string representation of this line
     */
    public String toString() {
        return String.format(
                "%s, Starting Station : %s, Terminus : %s, Time : %s",
                name, terminalStationDeparture, terminalStationArrival, starts);
    }

    /**
     * Returns an ArrayList containing all the stations in this line.
     *
     * @return an ArrayList of Station objects representing all the stations in this line.
     */
    public ArrayList<Station> getAllStations() {
        return allStations;
    }
}
