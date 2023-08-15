package hubertmap.view;

import hubertmap.controller.Controller;
import hubertmap.model.Time;
import hubertmap.model.transport.Line;
import hubertmap.model.transport.Station;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.SortedSet;
import javax.swing.*;

/**
 * The SchedulesPanel class displays the schedules of a given station in a table format. It allows
 * the user to select the departure or arrival station by clicking on a button. The schedules are
 * sorted by time, with duplicates removed.
 */
public class SchedulesPanel extends JPanel {

    /**
     * Creates a new SchedulesPanel with the schedules of the given station and lines.
     *
     * @param v the station whose schedules will be displayed
     * @param lines the lines that serve the station
     */
    public SchedulesPanel(Station v, HashMap<String, Line> lines) {
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(new GridBagLayout());
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        c.gridheight = 1;

        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        c.gridwidth = 1;
        c.gridy = 0;
        c.gridx = 0;

        JPanel station = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel stationlabel = new JLabel("Station: " + v.toString());
        station.add(stationlabel);
        this.add(station);

        JPanel buttons = new JPanel();
        JButton departure = new JButton("Departure");
        JButton arrival = new JButton("Arrival");
        c.gridx = 1;
        buttons.add(departure);
        buttons.add(arrival);
        this.add(buttons);

        departure.addActionListener(
                e -> {
                    Controller.setDeparture(v.getName());
                });

        arrival.addActionListener(
                e -> {
                    Controller.setArrival(v.getName());
                });

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 2;
        c.gridheight = GridBagConstraints.REMAINDER; // set to REMAINDER to span to bottom
        c.gridy = 1;
        c.gridx = 0;

        JPanel PlanPanel = new JPanel();
        PlanPanel.setLayout(new GridLayout(0, 2, 2, 2));

        ArrayList<String[]> data = new ArrayList<>();
        for (Entry<String, SortedSet<Time>> entry : v.getSchedules().entrySet()) {
            if (entry.getValue() != null)
                for (Time time : entry.getValue()) {
                    Line line = lines.get(entry.getKey());
                    Station terminalStation = line.getTerminalStationArrival();
                    data.add(new String[] {terminalStation.getName(), time.toString()});
                }
        }

        HashSet<String> set = new HashSet<>();
        int i = 0;
        while (i < data.size()) {
            String[] element = data.get(i);
            if (set.contains(element[1])) {
                data.remove(i);
            } else {
                set.add(element[1]);
                i++;
            }
        }

        int startIndex = 0;
        int endIndex = getRowTime(data) - 5;
        if (endIndex < startIndex) {
            endIndex = startIndex;
        }

        data.subList(startIndex, endIndex).clear();

        startIndex = getRowTime(data) + 10;
        endIndex = data.size();
        if (endIndex < startIndex) {
            endIndex = startIndex;
        }

        data.subList(startIndex, endIndex).clear();

        for (String[] strings : data) {
            JPanel way = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JPanel time = new JPanel();

            JLabel wayL = new JLabel(strings[0]);
            wayL.setPreferredSize(new Dimension(110, 15));

            JLabel timeL = new JLabel(strings[1]);
            timeL.setPreferredSize(new Dimension(60, 15));

            way.add(wayL);
            time.add(timeL);

            PlanPanel.add(way);
            PlanPanel.add(time);
        }
        JScrollPane scrollPane = new JScrollPane(PlanPanel);

        scrollPane.setPreferredSize(new Dimension(180, 450));
        this.add(scrollPane, c);
    }

    /**
     * Returns the index of the first row in the given JTable that has a time after the current
     * time. If there is no such row, returns -1.
     *
     * @param table the JTable to search
     * @return the index of the first row with a time after the current time, or -1 if there is no
     *     such row
     */
    private int getRowTime(ArrayList<String[]> data) {
        LocalTime currentTime = LocalTime.now();

        for (int i = 0; i < data.size(); i++) {
            String timeString = (String) data.get(i)[1];
            LocalTime rowTime = LocalTime.parse(timeString);

            if (rowTime.isAfter(currentTime)) {
                return i;
            }
        }
        return -1;
    }
}
