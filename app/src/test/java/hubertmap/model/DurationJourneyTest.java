package hubertmap.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DurationJourneyTest {

    @Test
    void checkDurationJourneyInitialization() {
        DurationJourney dj = new DurationJourney(150);
        assertTrue(dj.getMinute() == 2);
        assertTrue(dj.getSeconde() == 30);
    }
}
