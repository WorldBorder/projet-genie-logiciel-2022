/**
 * This class represents a station in a transportation system. A station has a name, coordinates (x
 * and y), and a list of all transportation lines that serve it.
 */
package hubertmap.model.transport;

import hubertmap.model.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class represents a station in a transportation system. A station has a name, coordinates (x
 * and y), and a list of all transportation lines that serve it.
 */
public class Station extends VertexTransport {
    private ArrayList<String> allLines;
    private Map<String, SortedSet<Time>> schedules = new HashMap<>();
    private String simpleLineName; // line name without variant
    private boolean multiLine; // used for the view

    /**
     * Constructs a new station with the given name, line, and coordinates. The station is served by
     * only one transportation line.
     *
     * @param name the name of the station
     * @param Line the transportation line that serves the station
     * @param x the x-coordinate of the station's location
     * @param y the y-coordinate of the station's location
     */
    public Station(String name, String Line, Float x, Float y) {
        super(name, x, y);
        this.allLines = new ArrayList<String>();
        this.allLines.add(Line);
        schedules.put(Line, null);
        this.simpleLineName = Line.split(" ")[0];
        this.multiLine = false;
    }

    /**
     * Returns the list of all transportation lines that serve the station.
     *
     * @return the list of all transportation lines that serve the station
     */
    public ArrayList<String> getAllLines() {
        return allLines;
    }

    /**
     * returns line name without variant
     *
     * @return String line name without variant
     */
    public String getSimpleLineName() {
        return simpleLineName;
    }

    /**
     * Returns true if there are more than one station with the same name
     *
     * @return True if there are
     */
    public boolean isMultiLine() {
        return multiLine;
    }

    /**
     * Indicates that there are more than one station with the same name
     *
     * @param multiLine boolean value for set multiLine
     */
    public void setMultiLine(boolean multiLine) {
        this.multiLine = multiLine;
    }

    /**
     * Returns the list of all transportation lines that serve the station.
     *
     * @return the list of all transportation lines that serve the station
     */
    public Map<String, SortedSet<Time>> getSchedules() {
        return schedules;
    }

    /**
     * Adds a schedule for the given line at this station.
     *
     * @param line the line to add the schedule for
     * @param time the time to add to the schedule
     */
    public void addSchedule(Line line, Time time) {
        if (schedules.get(line.getName()) == null) {
            schedules.put(line.getName(), new TreeSet<>());
        }
        SortedSet<Time> times = schedules.get(line.getName());
        times.add(time);
        schedules.put(line.getName(), times);
    }

    /**
     * Add a line to the station schedules. If the line already exists in the schedules, then this
     * method does nothing.
     *
     * @param lineName the name of the line to add
     */
    public void addLine(String lineName) {
        boolean exist = false;
        for (String line : schedules.keySet()) {
            if (line.contains(lineName)) {
                exist = true;
                break;
            }
        }
        if (!exist) allLines.add(lineName);
        schedules.put(lineName, null);
    }

    /**
     * This method returns a list of unique line numbers found in a list of strings.
     *
     * @return an ArrayList of unique line numbers in the list of strings
     */
    public ArrayList<String> getLinesNumbers() {
        Set<String> linesNumbers = new HashSet<String>();
        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            int index = line.indexOf(" ");
            if (index != -1) {
                line = line.substring(0, index);
                linesNumbers.add(line);
            }
        }
        return new ArrayList<String>(linesNumbers);
    }
}
