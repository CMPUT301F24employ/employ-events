package com.example.employ_events.ui.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.employ_events.R;

/**
 * Represents a notification related to an event
 */
public class Notification {
    private String eventID;
    private String message;
    private boolean invitation, cancellation, waitingList, read;
    NotificationCompat.Builder builder;
    private String CHANNEL_ID = "Organizer Notification";
    private Integer NOTIFICATION_ID = 0;
    NotificationManager notificationManager;
    Intent intent;

    /**
     * Default constructor for Notification
     */
    public Notification() {

    }

    /**
     * Constructs a Notification with specified event ID, message, and invitation status.
     *
     * @param eventID     the ID of the event associated with this notification
     * @param message,  the message of the notification
     * @param read   true if this notification has been read, false otherwise
     */
    public Notification(String eventID, String message, boolean read) {
        this.eventID = eventID;
        this.message = message;
        this.read = read;

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
    public void sendNotification(Context context){
        intent = new Intent(context, Notification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.white_notification)
                .setContentTitle("Employ Events Notification")
                .setContentText(this.message)
                // Set the intent that fires when the user taps the notification.
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager = context.getSystemService(NotificationManager.class);
        // TODO: 2024-11-23 check notification permissions before executing
        notificationManager.notify(NOTIFICATION_ID, builder.build());


    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}