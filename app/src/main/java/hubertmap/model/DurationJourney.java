package hubertmap.model;

/*  Time given in station csv file is in a specific format : NN:NN
 ** For example : 5:53 means 55 sec and 3 tenths of a second
 ** 12:45 => 1 minute, 24 second and 5 tenths of a second
 ** In this file we're not going to worry about tenths of a second
 ** because user doesn't need to have such precision
 */
/** The DurationJourney class represents a duration in minutes and secondes */
public class DurationJourney {

    /** The minute component of the duration */
    private int minute;

    /** The seconde component of the duration */
    private int seconde;

    /**
     * Constructs a DurationJourney object with the specified minute and seconde.
     *
     * @param leftTimeOperator the minute component
     * @param rightTimeOperator the seconde component
     */
    public DurationJourney(String leftTimeOperator, String rightTimeOperator) {
        if (leftTimeOperator.length() == 2) {
            minute = Character.getNumericValue(leftTimeOperator.charAt(0));
            seconde =
                    Integer.parseInt(
                            String.valueOf(leftTimeOperator.charAt(1))
                                    .concat(String.valueOf(rightTimeOperator.charAt(0))));
        } else {
            minute = 0;
            seconde =
                    Integer.parseInt(
                            String.valueOf(leftTimeOperator.charAt(0))
                                    .concat(String.valueOf(rightTimeOperator.charAt(0))));
        }
        this.minute += this.seconde / 60;
        this.seconde %= 60;
    }

    /**
     * Constructs a DurationJourney object with the specified seconde. It converts automatically the
     * minutes
     *
     * @param seconds the seconde component
     */
    public DurationJourney(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("seconds must be positive");
        }
        this.minute = seconds / 60;
        this.seconde = seconds % 60;
    }

    /**
     * Returns the seconde property
     *
     * @return the seconde propery
     */
    public int getSeconde() {
        return seconde;
    }

    /**
     * Sets the seconde property
     *
     * @param seconde the seconde property
     */
    public void setSeconde(int seconde) {
        this.seconde = seconde;
    }

    /**
     * Returns the minute property
     *
     * @return the minute property
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Sets the minute property
     *
     * @param minute the minute property
     */
    public void setMinute(int minute) {
        this.minute = minute;
    }

    /**
     * Returns a string representation of the time in the format "MM:SS".
     *
     * @return a string representation of the time
     */
    @Override
    public String toString() {
        return String.format("(minutes : %d, secondes : %d)", minute, seconde);
    }

    /**
     * @return value of duration in seconds
     */
    public int toSeconds() {
        return minute * 60 + seconde;
    }

    /**
     * Creates of DurationJourney object
     *
     * @return DurationJourney new object
     */
    public DurationJourney copy() {
        return new DurationJourney(this.toSeconds());
    }

    /**
     * adds minutes and seconds of parameter to current object, and returns it
     *
     * @param d DurationJourney to add
     * @return current object after the addition is done
     * @return DurationJourney new object
     */
    public DurationJourney add(DurationJourney d) {
        seconde += d.seconde;
        minute += d.minute + (seconde / 60);
        seconde %= 60;

        return this;
    }
}
