package com.example.employ_events.ui.invitation;

public class EventItem {
    private String eventId;
    private String eventName;

    // Constructor to initialize the eventId and eventName
    public EventItem(String eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
    }

    // Getter method for eventId
    public String getEventId() {
        return eventId;
    }

    // Getter method for eventName
    public String getEventName() {
        return eventName;
    }
}

