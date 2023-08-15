package hubertmap.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimeTest {

    @Test
    void checkIncreaseBySecondes() {
        Time t = new Time(0, 0, 0);
        t.increaseBySeconde(30);
        assertTrue(AreTimesEquals(t, new Time(0, 0, 30)));
        t.increaseBySeconde(61);
        assertTrue(AreTimesEquals(t, new Time(0, 1, 31)));
    }

    @Test
    void checkIncreaseByMinutes() {
        Time t = new Time(0, 0, 0);
        t.increaseByMinute(20);
        assertTrue(AreTimesEquals(t, new Time(0, 20, 0)));
        t.increaseByMinute(55);
        assertTrue(AreTimesEquals(t, new Time(1, 15, 0)));
    }

    @Test
    void checkIncreaseByHours() {
        Time t = new Time(0, 0, 0);
        t.increaseByHours(4);
        assertTrue(AreTimesEquals(t, new Time(4, 0, 0)));
        t.increaseByHours(30);
        assertTrue(AreTimesEquals(t, new Time(10, 0, 0)));
    }

    private boolean AreTimesEquals(Time t1, Time t2) {
        return t1.getHour() == t2.getHour()
                && t1.getMinute() == t2.getMinute()
                && t1.getSeconde() == t1.getSeconde();
    }
}
