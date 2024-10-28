package com.example.employ_events.ui.profile;

/**
 * This is a class that defines a user's Profile.
 */
public class Profile {
    private String uniqueID, name, email;
    private int phoneNumber;
    private boolean organizerNotifications, adminNotifications;
    private boolean isEntrant, isAdmin, isOrganizer;

    /**
     * Constructs a new Profile with the specified unique ID..
     *
     * @param uniqueID the unique identifier for the device
     */
    public Profile(String uniqueID) {
        this.uniqueID = uniqueID;
        this.organizerNotifications = false;
        this.adminNotifications = false;
        this.isEntrant = false;
        this.isOrganizer = false;
        this.isAdmin = false;
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
     * Returns the unique ID associated with this profile.
     *
     * @return the unique ID
     */
    public String getUniqueID() {
        return uniqueID;
    }
    /**
     * Indicates if the user is an entrant.
     *
     * @return true if user has joined a waiting list.
     */
    public boolean isEntrant() {
        return isEntrant;
    }

    /**
     * Sets the user's entrant role.
     *
     * @param entrant true to indicate user is an entrant, false otherwise.
     */
    public void setEntrant(boolean entrant) {
        isEntrant = entrant;
    }

    /**
     * Indicates if the user is an organizer.
     *
     * @return true if user is an organizer.
     */
    public boolean isOrganizer() {
        return isOrganizer;
    }

    /**
     * Sets the user's organizer role.
     *
     * @param organizer true to indicate user is an organizer, false otherwise.
     */
    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    /**
     * Indicates if the user is an admin.
     *
     * @return true if user is an admin.
     */
    public boolean isAdmin() {
        return isAdmin;
    }
}
