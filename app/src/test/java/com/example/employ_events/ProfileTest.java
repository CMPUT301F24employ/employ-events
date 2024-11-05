package com.example.employ_events;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.employ_events.ui.profile.Profile;

public class ProfileTest {
    private Profile profile;

    @Before
    public void setUp() {
        // Initialize the Profile object before each test
        profile = new Profile("uniqueID123");
    }

    @Test
    public void testConstructor_initialValues() {
        // Check the initial state of the profile created with uniqueID
        assertEquals("uniqueID123", profile.getUniqueID());
        assertFalse(profile.isEntrant());
        assertFalse(profile.isOrganizer());
        assertFalse(profile.isAdmin());
        assertFalse(profile.isCustomPFP());
    }

    @Test
    public void testSetName_getName() {
        // Test setting and getting the name
        profile.setName("Tina Machi");
        assertEquals("Tina Machi", profile.getName());
    }

    @Test
    public void testSetEmail_getEmail() {
        // Test setting and getting the email
        profile.setEmail("tina.machi@candy.com");
        assertEquals("tina.machi@candy.com", profile.getEmail());
    }

    @Test
    public void testSetPhoneNumber_getPhoneNumber() {
        // Test setting and getting the phone number
        profile.setPhoneNumber("123-456-7890");
        assertEquals("123-456-7890", profile.getPhoneNumber());
    }

    @Test
    public void testSetPfpURI_getPfpURI() {
        // Test setting and getting the profile picture URI
        profile.setPfpURI("http://example.com/image.jpg");
        assertEquals("http://example.com/image.jpg", profile.getPfpURI());
    }

    @Test
    public void testSetCustomPFP_getCustomPFP() {
        // Test setting and checking custom profile picture status
        profile.setCustomPFP(true);
        assertTrue(profile.isCustomPFP());

        profile.setCustomPFP(false);
        assertFalse(profile.isCustomPFP());
    }

    @Test
    public void testSetEntrant_isEntrant() {
        // Test setting and getting entrant status
        profile.setEntrant(true);
        assertTrue(profile.isEntrant());

        profile.setEntrant(false);
        assertFalse(profile.isEntrant());
    }

    @Test
    public void testSetOrganizer_isOrganizer() {
        // Test setting and getting organizer status
        profile.setOrganizer(true);
        assertTrue(profile.isOrganizer());

        profile.setOrganizer(false);
        assertFalse(profile.isOrganizer());
    }

    @Test
    public void test_isAdmin() {
        // Test that user is not an admin.
        assertFalse(profile.isAdmin());
    }
}
