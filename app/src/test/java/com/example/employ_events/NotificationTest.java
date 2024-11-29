package com.example.employ_events;
import com.example.employ_events.ui.notifications.Notification;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Tina
 * Unit test class for the Notification class.
 * Tests various aspects of the Notification class, such as constructors, getters, and setters.
 */
public class NotificationTest {


    private Notification notification; // Our Notification class

    /**
     * Sets up the test environment by creating a new Notification instance
     * with some sample data before each test.
     */
    @Before
    public void setUp() {
        // Create a new instance of Notification with some sample data
        notification = new Notification("eventID", "This is a test message - you have been invited!", false);
    }

    /**
     * Tests the constructor of the Notification class, checking that it
     * correctly sets the event ID, message, and read status.
     */
    @Test
    public void testNotificationConstructorWithEventID() {
        // Test if constructor correctly sets the event ID and message
        assertEquals("eventID", notification.getEventID());
        assertEquals("This is a test message - you have been invited!", notification.getMessage());
        assertFalse(notification.isRead());
    }

    /**
     * Tests the setEventID method to ensure it correctly sets the event ID.
     */
    @Test
    public void testSetEventID() {
        notification.setEventID("newEventID");
        assertEquals("newEventID", notification.getEventID());
    }

    /**
     * Tests the setMessage method to ensure it correctly sets the message.
     */
    @Test
    public void testSetMessage() {
        notification.setMessage("This is a test message - you got cancelled!");
        assertEquals("This is a test message - you got cancelled!", notification.getMessage());
    }

    /**
     * Tests the setInvitation method to ensure it correctly sets the invitation flag.
     */
    @Test
    public void testSetInvitation() {
        notification.setInvitation(true);
        assertTrue(notification.isInvitation());
    }

    /**
     * Tests the setRead method to ensure it correctly sets the read status.
     */
    @Test
    public void testSetRead() {
        notification.setRead(true);
        assertTrue(notification.isRead());
    }
}
