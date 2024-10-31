package com.example.employ_events.ui.profile;

/**
 * This is a class that defines a user's Profile.
 */
public class Profile {
    private String uniqueID, name, email, pfpURI, phoneNumber;
    private boolean isEntrant, isAdmin, isOrganizer, customPFP;

    /**
     * Constructs a new Profile with the specified unique ID..
     *
     * @param uniqueID the unique identifier for the device
     */
    public Profile(String uniqueID) {
        this.uniqueID = uniqueID;
        this.isEntrant = false;
        this.isOrganizer = false;
        this.isAdmin = false;
        this.customPFP = false;
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
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber the new phone number of the user
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getPfpURI() {
        return pfpURI;
    }

    public void setPfpURI(String pfpURI) {
        this.pfpURI = pfpURI;
    }

    public boolean isCustomPFP() {
        return customPFP;
    }

    public void setCustomPFP(boolean customPFP) {
        this.customPFP = customPFP;
    }
}
