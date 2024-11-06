package com.example.employ_events.ui.entrants;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an entrant for an event. An entrant has a name, unique ID, email,
 * and status regarding their placement on various lists (waiting list, cancelled list, etc.).
 */
public class Entrant {
    private String name, uniqueID, email;
    private Boolean onWaitingList;
    private Boolean onCancelledList;
    private Boolean onAcceptedList;
    private Boolean onRegisteredList;

    /**
     * Default constructor for creating an empty Entrant object.
     */
    public Entrant() { }

    /**
     * Constructs an Entrant object with a specified name.
     * @param name The name of the entrant.
     */
    public Entrant(String name) {
        this.name = name;
    }

    /**
     * Gets the email of the entrant.
     * @return The email of the entrant.
     */
    public String getEmail() { return email; }

    /**
     * Sets the email of the entrant.
     * @param email The email to set for the entrant.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the unique ID of the entrant.
     * @return The unique ID of the entrant.
     */
    public String getUniqueID() { return uniqueID; }

    /**
     * Sets the unique ID for the entrant.
     * @param uniqueID The unique ID to set for the entrant.
     */
    public void setUniqueID(String uniqueID) { this.uniqueID = uniqueID; }

    /**
     * Gets the name of the entrant.
     * @return The name of the entrant.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the entrant.
     * @param name The name to set for the entrant.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the status of whether the entrant is on the waiting list.
     * @return True if the entrant is on the waiting list, false otherwise.
     */
    public Boolean getOnWaitingList() {
        return onWaitingList;
    }

    /**
     * Sets the status of whether the entrant is on the waiting list.
     * @param onWaitingList The status to set for the waiting list.
     */
    public void setOnWaitingList(Boolean onWaitingList) {
        this.onWaitingList = onWaitingList;
    }

    /**
     * Gets the status of whether the entrant is on the cancelled list.
     * @return True if the entrant is on the cancelled list, false otherwise.
     */
    public Boolean getOnCancelledList() {
        return onCancelledList;
    }

    /**
     * Sets the status of whether the entrant is on the cancelled list.
     * @param onCancelledList The status to set for the cancelled list.
     */
    public void setOnCancelledList(Boolean onCancelledList) {
        this.onCancelledList = onCancelledList; }

    /**
     * Gets the status of whether the entrant is on the accepted list.
     * @return True if the entrant is on the accepted list, false otherwise.
     */
    public Boolean getOnAcceptedList() {
        return onAcceptedList;
    }

    /**
     * Sets the status of whether the entrant is on the accepted list.
     * @param onAcceptedList The status to set for the accepted list.
     */
    public void setOnAcceptedList(Boolean onAcceptedList) {
        this.onAcceptedList = onAcceptedList;
    }

    /**
     * Gets the status of whether the entrant is on the registered list.
     * @return True if the entrant is on the registered list, false otherwise.
     */
    public Boolean getOnRegisteredList() {
        return onRegisteredList;
    }

    /**
     * Sets the status of whether the entrant is on the registered list.
     * @param onRegisteredList The status to set for the registered list.
     */
    public void setOnRegisteredList(Boolean onRegisteredList) {
        this.onRegisteredList = onRegisteredList;
    }

    /**
     * Converts the Entrant object to a Map. This is useful for storing the
     * entrant's data in a database or for easy serialization.
     * @return A map representing the Entrant's data, with field names as keys and values as map values.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", this.name);
        map.put("email", this.email);
        map.put("onRegisteredList", this.getOnRegisteredList());
        map.put("onAcceptedList", this.getOnAcceptedList());
        map.put("onCancelledList", this.getOnCancelledList());
        map.put("onWaitingList", this.getOnWaitingList());
        // Add other fields as needed
        return map;
    }
}
