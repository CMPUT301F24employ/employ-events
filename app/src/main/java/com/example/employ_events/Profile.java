package com.example.employ_events;

import android.provider.ContactsContract;

/**
 * This is a class that defines a user's Profile.
 */
public class Profile {
    private String userID, name, email;
    private int phoneNumber;
    private boolean organizerNotifications, adminNotifications;

    public Profile(String userID, String name, String email) {
        this.name = name;
        this.email = email;
        this.userID = userID;
        this.organizerNotifications = true;
        this.adminNotifications = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isOrganizerNotifications() {
        return organizerNotifications;
    }

    public void setOrganizerNotifications(boolean organizerNotifications) {
        this.organizerNotifications = organizerNotifications;
    }

    public boolean isAdminNotifications() {
        return adminNotifications;
    }

    public void setAdminNotifications(boolean adminNotifications) {
        this.adminNotifications = adminNotifications;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
