package com.example.employ_events.ui.notifications;

/**
 * Represents a notification related to an event
 */
public class Notification {
    private String eventID;
    private String message;
    private boolean invitation;

    /**
     * Default constructor for Notification
     */
    public Notification() {

    }

    /**
     * Constructs a Notification with specified event ID, message, and invitation status.
     *
     * @param eventID     the ID of the event associated with this notification
     * @param message     the message of the notification
     * @param invitation  true if this notification is an invitation, false otherwise
     */
    public Notification(String eventID, String message, boolean invitation) {
        this.eventID = eventID;
        this.message = message;
        this.invitation = invitation;
    }

    /**
     * Gets the event ID associated with this notification.
     *
     * @return the event ID
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Sets the event ID for this notification.
     *
     * @param eventID the event ID to set
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Gets the message of this notification.
     *
     * @return the notification message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message of this notification.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Checks if this notification is an invitation.
     *
     * @return true if this is an invitation, false otherwise
     */
    public boolean isInvitation() {
        return invitation;
    }

    /**
     * Sets the invitation status of this notification.
     *
     * @param invitation true if this is an invitation, false otherwise
     */
    public void setInvitation(boolean invitation) {
        this.invitation = invitation;
    }
}