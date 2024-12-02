package com.example.employ_events;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.employ_events.model.Facility;

public class FacilityTest {
    private Facility facility;

    //US 02.01.03	As an organizer, I want to create and manage my facility profile

    // Set up method to initialize a Facility object with default values for testing
    @Before
    public void setUp() {
        facility = new Facility("UoA", "UoA@example.com", "UoA Campus", "organizer_UoA");
    }

    // Test the Facility constructor with four parameters
    @Test
    public void testConstructorWithFourParameters() {
        // Verify that the constructor correctly sets each field
        assertEquals("UoA", facility.getName());
        assertEquals("UoA@example.com", facility.getEmail());
        assertEquals("UoA Campus", facility.getAddress());
        assertEquals("organizer_UoA", facility.getOrganizer_id());
        // Check that optional fields are null by default
        assertNull(facility.getPhone_number());
        assertNull(facility.getFacilityPfpUri());
    }

    // Test the Facility constructor with five parameters, including a phone number
    @Test
    public void testConstructorWithFiveParameters() {
        // Initialize a Facility object with an additional phone number parameter
        Facility facilityWithPhone = new Facility("UoA Gym", "gym@uoa.com", "VV",
                "organizer_vv", "555-1234");
        // Verify that all provided parameters are set correctly
        assertEquals("UoA Gym", facilityWithPhone.getName());
        assertEquals("gym@uoa.com", facilityWithPhone.getEmail());
        assertEquals("VV", facilityWithPhone.getAddress());
        assertEquals("organizer_vv", facilityWithPhone.getOrganizer_id());
        assertEquals("555-1234", facilityWithPhone.getPhone_number());
        // Check that optional fields, such as the profile picture URI, are null by default
        assertNull(facilityWithPhone.getFacilityPfpUri());
    }

    // Test the setter and getter methods for each field
    @Test
    public void testSettersAndGetters() {
        facility.setName("Updated UoA");
        facility.setEmail("updatedUoA@example.com");
        facility.setAddress("New Campus");
        facility.setOrganizer_id("new_organizer_UoA");
        facility.setPhone_number("555-5678");
        facility.setFacilityPfpUri("http://uoa.com/pfp.png");

        assertEquals("Updated UoA", facility.getName());
        assertEquals("updatedUoA@example.com", facility.getEmail());
        assertEquals("New Campus", facility.getAddress());
        assertEquals("new_organizer_UoA", facility.getOrganizer_id());
        assertEquals("555-5678", facility.getPhone_number());
        assertEquals("http://uoa.com/pfp.png", facility.getFacilityPfpUri());
    }
}
