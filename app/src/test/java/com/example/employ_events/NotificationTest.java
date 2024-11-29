package com.example.employ_events;
import com.example.employ_events.ui.notifications.Notification;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class NotificationTest {

    private Notification notification; // Our Notification class

    @Before
    public void setUp() {
        // Create a new instance of Notification with some sample data
        notification = new Notification("eventID", "This is a test message", false);
    }

    @Test
    public void testNotificationConstructorWithEventID() {
        // Test if constructor correctly sets the event ID and message
        assertEquals("eventID", notification.getEventID());
        assertEquals("This is a test message", notification.getMessage());
        assertFalse(notification.isRead());
    }

    @Test
    public void testSetEventID() {
        notification.setEventID("newEventID");
        assertEquals("newEventID", notification.getEventID());
    }

    @Test
    public void testSetMessage() {
        notification.setMessage("Updated message");
        assertEquals("Updated message", notification.getMessage());
    }

    @Test
    public void testSetInvitation() {
        notification.setInvitation(true);
        assertTrue(notification.isInvitation());
    }

    @Test
    public void testSetRead() {
        notification.setRead(true);
        assertTrue(notification.isRead());
    }


}
