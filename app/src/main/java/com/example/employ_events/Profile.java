package com.example.employ_events;

/**
 * This is a class that defines a user's Profile.
 */
public class Profile {
    private String deviceID, name, email;
    private int phoneNumber;
    private boolean organizerNotifications, adminNotifications;

    /**
     * Constructs a new Profile with the specified device ID, name, and email.
     *
     * @param deviceID the unique identifier for the device
     * @param name the name of the user
     * @param email the email address of the user
     */
    public Profile(String deviceID, String name, String email) {
        this.name = name;
        this.email = email;
        this.deviceID = deviceID;
        this.organizerNotifications = false;
        this.adminNotifications = false;
    }

    /**
     * Returns the name of the user.
     *
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name the new name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the email address of the user.
     *
     * @return the email address of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the new email address of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the phone number of the user.
     *
     * @return the phone number of the user
     */
    public int getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber the new phone number of the user
     */
    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Indicates whether the user wants to receive organizer notifications.
     *
     * @return true if organizer notifications are enabled, false otherwise
     */
    public boolean isOrganizerNotifications() {
        return organizerNotifications;
    }

    /**
     * Sets the user's preference for organizer notifications.
     *
     * @param organizerNotifications true to enable organizer notifications, false to disable
     */
    public void setOrganizerNotifications(boolean organizerNotifications) {
        this.organizerNotifications = organizerNotifications;
    }

    /**
     * Indicates whether the user wants to receive admin notifications.
     *
     * @return true if admin notifications are enabled, false otherwise
     */
    public boolean isAdminNotifications() {
        return adminNotifications;
    }

    /**
     * Sets the user's preference for admin notifications.
     *
     * @param adminNotifications true to enable admin notifications, false to disable
     */
    public void setAdminNotifications(boolean adminNotifications) {
        this.adminNotifications = adminNotifications;
    }

    /**
     * Returns the device ID associated with this profile.
     *
     * @return the device ID
     */
    public String getDeviceID() {
        return deviceID;
    }

}
