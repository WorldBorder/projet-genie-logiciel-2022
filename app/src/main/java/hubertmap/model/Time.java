package hubertmap.model;

/** The Time class represents a time of day in hours and minutes. */
public class Time implements Comparable<Time> {
    /** The hour component of the time. */
    private int hour;

    /** The minute component of the time. */
    private int minute;

    /** The minute component of the time. */
    private int seconde;

    /**
     * Constructs a Time object with the specified hour and minute.
     *
     * @param hour the hour component of the time
     * @param minute the minute component of the time
     * @param seconde the seconde component of the time
     */
    public Time(int hour, int minute, int seconde) {

        this.hour = hour;
        this.minute = minute;
        this.seconde = seconde;
    }

    /**
     * Constructs a new Time object by copying the fields of another Time object.
     *
     * @param time the Time object to copy
     */
    public Time(Time time) {

        this.hour = time.hour;
        this.minute = time.minute;
        this.seconde = time.seconde;
    }
    /**
     * Increases the time by the specified duration journey.
     *
     * @param dj the duration journey to be added to the current time
     * @return a new Time object with the time increased by the specified duration journey
     */
    public Time increaseWithADurationJourney(DurationJourney dj) {
        this.increaseBySeconde(dj.getSeconde());
        this.increaseByMinute(dj.getMinute());
        return new Time(this.hour, this.minute, this.seconde);
    }

    /**
     * Returns the hour component of the time.
     *
     * @return the hour component of the time
     */
    public int getHour() {
        return hour;
    }

    /**
     * Sets the hour component of the time.
     *
     * @param hour the hour component of the time
     */
    public void setHour(int hour) {
        this.hour = hour;
    }
    /**
     * Increases the time by a given number of hours.
     *
     * @param hour the number of hours to add to the time
     */
    public void increaseByHours(int hour) {
        this.hour = (this.hour + hour) % 24;
    }

    /**
     * Returns the minute component of the time.
     *
     * @return the minute component of the time
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Sets the minute component of the time.
     *
     * @param minute the minute component of the time
     */
    public void setMinute(int minute) {
        this.minute = minute;
    }

    /**
     * Returns the seconde component of the time.
     *
     * @return the seconde component of the time
     */
    public int getSeconde() {
        return hour;
    }
    /**
     * Increases the time object by a specified number of minutes.
     *
     * @param minute the number of minutes to add to the time object
     */
    public void increaseByMinute(int minute) {
        this.minute += minute;
        while (this.minute > 59) {
            this.minute -= 60;
            this.increaseByHours(1);
        }
    }

    /**
     * Sets the seconde component of the time.
     *
     * @param seconde the seconde component of the time
     */
    public void setSeconde(int seconde) {
        this.seconde = seconde;
    }

    /**
     * Increases the time by a given number of seconds. If the seconds exceed 59, the method adds 1
     * minute to the time and adjusts the second count accordingly.
     *
     * @param seconde The number of seconds to add to the time.
     */
    public void increaseBySeconde(int seconde) {
        this.seconde += seconde;
        while (this.seconde > 59) {
            this.seconde -= 60;
            this.increaseByMinute(1);
        }
    }

    /**
     * Returns a string representation of the time in the format "HH:MM".
     *
     * @return a string representation of the time
     */
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hour, minute, seconde);
    }

    /**
     * Compares this Time object with the specified Time object for order. Returns a negative
     * integer, zero, or a positive integer as this object is less than, equal to, or greater than
     * the specified object.
     *
     * @param other the Time object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal
     *     to, or greater than the specified object
     */
    @Override
    public int compareTo(Time other) {
        if (this.hour != other.hour) {
            return this.hour - other.hour;
        }
        if (this.minute != other.minute) {
            return this.minute - other.minute;
        }
        if (this.seconde != other.seconde) {
            return this.seconde - other.seconde;
        }
        return 0;
    }
}
