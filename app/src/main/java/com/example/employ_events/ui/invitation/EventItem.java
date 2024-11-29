package com.example.employ_events.ui.invitation;
/*
Used for displaying in an item in a list.
 */

/**
 * @author Tina
 * Represents an event with an ID and a name.
 */
public class EventItem {
    private String eventId;
    private String eventName;

    /**
     * Constructs an EventItem with the specified event ID and event name.
     * @param eventId   the unique identifier of the event
     * @param eventName the name of the event
     */
    public EventItem(String eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
    }

    /**
     * Returns the event ID.
     * @return the unique identifier of the event
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Returns the event name.
     * @return the name of the event
     */
    public String getEventName() {
        return eventName;
    }
}

