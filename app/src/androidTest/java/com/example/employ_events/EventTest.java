package com.example.employ_events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.employ_events.databinding.EventDetailsBinding;
import com.example.employ_events.ui.entrants.Entrant;
import com.example.employ_events.ui.events.Event;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class EventTest {
    public Event mockEvent(){
        Event event = new Event();
        ArrayList<Entrant> entrantsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Entrant entrant = new Entrant();
            entrant.setOnWaitingList(true); // Set initial waiting list status
            entrant.setOnCancelledList(false); // Ensure they are not cancelled
            event.addEntrant(entrant);
        }
        return event;
    }
    @Test
    public void testGenerateSample() {
        Event event = mockEvent();
        event.setEventCapacity(5);
        event.generateSample();

        // Count entrants marked as accepted
        long acceptedCount = event.getEntrantsList().stream()
                .filter(Entrant::getOnAcceptedList)
                .count();

        // Verify the number of accepted entrants does not exceed event capacity
        assertEquals(acceptedCount, 5);

    }
}
