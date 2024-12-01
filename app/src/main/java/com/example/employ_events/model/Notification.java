package com.example.employ_events.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.employ_events.R;
import com.example.employ_events.ui.fragment.invitation.InvitationsListFragment;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

/**
 * @author Sahara, Tina, Aasvi
 * Represents a notification for event-related actions, such as invitations, cancellations, or waitlist updates.
 * Provides functionality for creating and sending notifications to users.
 */
public class Notification {
    private String eventID, message, CHANNEL_ID;
    private boolean invitation, cancellation, waitingList, read;
    private NotificationCompat.Builder builder;
    private Integer NOTIFICATION_ID = 0;
    private NotificationManager notificationManager;
    private Intent intent;

    /**
     * Constructs a Notification object with the specified event ID, message, and read status.
     *
     * @param eventID the unique identifier of the event associated with the notification
     * @param message the message content of the notification
     * @param read    indicates whether the notification has been read
     */
    public Notification(String eventID, String message, boolean read, String CHANNEL_ID) {
        this.eventID = eventID;
        this.message = message;
        this.read = read;
        this.CHANNEL_ID = CHANNEL_ID;
    }

    /**
     * Gets the event ID associated with the notification.
     * @return the event ID
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Sets the event ID associated with the notification.
     * @param eventID the event ID to set
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Gets the message content of the notification.
     * @return the notification message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message content of the notification.
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Checks whether the notification is an invitation.
     * @return true if it's an invitation, false otherwise
     */
    public boolean isInvitation() {
        return invitation;
    }

    /**
     * Sets whether the notification is an invitation.
     * @param invitation true if it's an invitation, false otherwise
     */
    public void setInvitation(boolean invitation) {
        this.invitation = invitation;
    }

    /**
     * Checks whether the notification has been read.
     * @return true if the notification has been read, false otherwise
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets the read status of the notification.
     * @param read true if the notification has been read, false otherwise
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    public String getCHANNEL_ID() {
        return CHANNEL_ID;
    }

    public void setCHANNEL_ID(String CHANNEL_ID) {
        this.CHANNEL_ID = CHANNEL_ID;
    }

    /**
     * Sends a notification to the user.
     * This method creates the notification and posts it to the system's notification manager.
     *
     * @param context the application context
     */
    public void sendNotification(Context context) {
        // Ensure the context is valid
        if (context == null) {
            return;
        }

        intent = new Intent(context, InvitationsListFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create a pending intent for the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.white_notification)
                .setContentTitle("Employ Events Notification")
                .setContentText(this.message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // Ensures the notification gets cleared when tapped

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the app has permission to post notifications
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        } else {
            System.out.println("Notification permission not granted.");
        }
    }
}
