package com.example.employ_events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.employ_events.model.Entrant;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class EntrantTest {
    private Entrant entrant;

    @Before
    public void setUp() {
        entrant = new Entrant("Candy Land");
    }

    @Test
    public void testConstructor() {
        assertEquals("Candy Land", entrant.getName());
        assertFalse(entrant.getOnAcceptedList());
        assertFalse(entrant.getOnCancelledList());
        assertFalse(entrant.getOnWaitingList());
        assertFalse(entrant.getOnRegisteredList());
    }

    @Test
    public void testSetAndGetName() {
        entrant.setName("Land Candy");
        assertEquals("Land Candy", entrant.getName());
    }

    @Test
    public void testSetAndGetEmail() {
        entrant.setEmail("candy.land@example.com");
        assertEquals("candy.land@example.com", entrant.getEmail());
    }

    @Test
    public void testSetAndGetUniqueID() {
        entrant.setUniqueID("1A2BC3");
        assertEquals("1A2BC3", entrant.getUniqueID());
    }

    @Test
    public void testSetAndGetOnWaitingList() {
        entrant.setOnWaitingList(true);
        assertTrue(entrant.getOnWaitingList());
        entrant.setOnWaitingList(false);
        assertFalse(entrant.getOnWaitingList());
    }

    @Test
    public void testSetAndGetOnCancelledList() {
        entrant.setOnCancelledList(true);
        assertTrue(entrant.getOnCancelledList());
        entrant.setOnCancelledList(false);
        assertFalse(entrant.getOnCancelledList());
    }

    @Test
    public void testSetAndGetOnAcceptedList() {
        entrant.setOnAcceptedList(true);
        assertTrue(entrant.getOnAcceptedList());
        entrant.setOnAcceptedList(false);
        assertFalse(entrant.getOnAcceptedList());
    }

    @Test
    public void testSetAndGetOnRegisteredList() {
        entrant.setOnRegisteredList(true);
        assertTrue(entrant.getOnRegisteredList());
        entrant.setOnRegisteredList(false);
        assertFalse(entrant.getOnRegisteredList());
    }

    @Test
    public void testToMap() {
        entrant.setEmail("candy.land@example.com");
        entrant.setOnWaitingList(true);
        entrant.setOnRegisteredList(true);
        Map<String, Object> map = entrant.toMap();

        assertNotNull(map);
        assertTrue(map.containsKey("name"));
        assertTrue(map.containsKey("email"));
        assertTrue(map.containsKey("onRegisteredList"));
        assertTrue(map.containsKey("onWaitingList"));

        // Verify correct values in map
        assertEquals("Candy Land", map.get("name"));
        assertEquals("candy.land@example.com", map.get("email"));
        assertEquals(true, map.get("onWaitingList"));
        assertEquals(true, map.get("onRegisteredList"));
    }
}
