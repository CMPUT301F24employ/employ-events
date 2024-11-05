package com.example.employ_events;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.employ_events.ui.facility.Facility;

public class FacilityTest {
    private Facility facility;

    @Before
    public void setUp() {
        facility = new Facility("UoA", "UoA@example.com", "UoA Campus", "organizer_UoA");
    }

    @Test
    public void testConstructorWithFourParameters() {
        assertEquals("UoA", facility.getName());
        assertEquals("UoA@example.com", facility.getEmail());
        assertEquals("UoA Campus", facility.getAddress());
        assertEquals("organizer_UoA", facility.getOrganizer_id());
        assertNull(facility.getPhone_number());
        assertNull(facility.getFacilityPfpUri());
    }

    @Test
    public void testConstructorWithFiveParameters() {
        Facility facilityWithPhone = new Facility("UoA Gym", "gym@uoa.com", "VV", "organizer_vv", "555-1234");
        assertEquals("UoA Gym", facilityWithPhone.getName());
        assertEquals("gym@uoa.com", facilityWithPhone.getEmail());
        assertEquals("VV", facilityWithPhone.getAddress());
        assertEquals("organizer_vv", facilityWithPhone.getOrganizer_id());
        assertEquals("555-1234", facilityWithPhone.getPhone_number());
        assertNull(facilityWithPhone.getFacilityPfpUri());
    }

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
