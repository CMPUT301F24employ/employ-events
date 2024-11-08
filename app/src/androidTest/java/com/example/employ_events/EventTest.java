package com.example.employ_events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.employ_events.ui.entrants.Entrant;
import com.example.employ_events.ui.events.Event;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class EventTest {
    private Event event;

    // Set up method to initialize the Event object with a mock list of entrants
    @Before
    public void setUp() {
        event = new Event();
        ArrayList<Entrant> entrantsList = new ArrayList<>();
        // Populate the entrants list with 10 entrants initially, all on the waiting list
        for (int i = 0; i < 10; i++) {
            Entrant entrant = new Entrant();
            entrant.setOnWaitingList(true);
            entrant.setOnCancelledList(false);
            entrant.setOnAcceptedList(false);
            entrantsList.add(entrant);
        }
        // Set the entrants list in the Event object
        event.setEntrantsList(entrantsList);
    }

    // US 02.05.02	As an organizer I want to set the system to sample a specified number of attendees to register for the event
    @Test
    public void testGenerateSample() {
        // Set event capacity to 5 for sampling purposes
        event.setEventCapacity(5);
        // Call generateSample without Firestore updates for testing
        event.generateSample(false);

        // Count the number of entrants marked as accepted
        long acceptedCount = event.getEntrantsList().stream()
                .filter(Entrant::getOnAcceptedList)
                .count();

        // Verify that exactly 5 entrants were accepted, matching the event capacity
        assertEquals(5, acceptedCount);
    }

    // Test for sampling when the waitlist is smaller than the event capacity
    @Test
    public void testGenerateSampleIfWaitlistLessThanCapacity() {
        // Set event capacity higher than the waitlist size to test behavior
        event.setEventCapacity(15);
        event.generateSample(false);

        long acceptedCount = event.getEntrantsList().stream()
                .filter(Entrant::getOnAcceptedList)
                .count();

        // Verify that all 10 entrants were accepted, since capacity exceeds the number of entrants
        assertEquals(10, acceptedCount);
    }

    // US 02.03.01	As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list
    @Test
    public void testWaitingListLimit() {
        event.setLimited(10); // Set a limit of 10 for the waiting list
        // Attempt to add an additional entrant beyond the limit
        Entrant entrant = new Entrant();
        // Verify that the new entrant could not be added, as the limit is reached
        assertFalse(event.addEntrant(entrant));
        // Confirm that the entrants list size is still at the limit (10)
        assertEquals(10, event.getEntrantsList().size());
    }

    // US 01.01.01	As an entrant, I want to join the waiting list for a specific event
    @Test
    public void testAddingToList() {
        Entrant entrant = new Entrant();
        // Verify that adding a new entrant is successful
        assertTrue(event.addEntrant(entrant));
        // Verify that the new entrant is marked as on the waiting list
        assertTrue(entrant.getOnWaitingList());
        // Confirm that the entrants list size has increased to 11
        assertEquals(11, event.getEntrantsList().size());
    }
}
