package com.example.employ_events;

import com.example.employ_events.ui.events.Event;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.employ_events.ui.events.Event;
import com.example.employ_events.ui.entrants.Entrant;

import java.util.ArrayList;
import java.util.Date;

class EventTest {
    private Event event;
    private ArrayList<Entrant> entrantsList;

    @BeforeEach
    void setUp() {
        // Create a sample event with a capacity
        event = new Event();
        event.setEventCapacity(5); // Event capacity set to 5

        // Initialize entrants
        entrantsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Entrant entrant = new Entrant();
            entrant.setOnWaitingList(true); // Set initial waiting list status
            entrant.setOnCancelledList(false); // Ensure they are not cancelled
            event.addEntrant(entrant);
        }

    }

    @Test
    void testGenerateSample() {
        event.generateSample();

        // Count entrants marked as accepted
        long acceptedCount = event.getEntrantsList().stream()
                .filter(Entrant::getOnAcceptedList)
                .count();

        // Verify the number of accepted entrants does not exceed event capacity
        assertTrue(acceptedCount <= event.getEventCapacity(), "Accepted count exceeds capacity");

        // Ensure no cancelled entrants are marked as accepted
        for (Entrant entrant : event.getEntrantsList()) {
            if (entrant.getOnCancelledList()) {
                assertFalse(entrant.getOnAcceptedList(), "Cancelled entrant marked as accepted");
            }
        }
    }
}