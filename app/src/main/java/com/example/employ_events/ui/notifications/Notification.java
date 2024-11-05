package com.example.employ_events.ui.notifications;

public class Notification {
    private String eventID;
    private String message;
    private boolean invitation;

    public Notification() {

    }

    public Notification(String eventID, String message, boolean invitation) {
        this.eventID = eventID;
        this.message = message;
        this.invitation = invitation;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isInvitation() {
        return invitation;
    }

    public void setInvitation(boolean invitation) {
        this.invitation = invitation;
    }
}