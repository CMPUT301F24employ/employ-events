package com.example.employ_events.ui.facility;

/**
 * Represents a facility with a name and an owner ID.
 */
public class Facility {
    String name, owner_id;

    /**
     * Constructs a Facility with the specified name and owner ID.
     *
     * @param name the name of the facility
     * @param owner_id the ID of the owner of the facility
     */
    public Facility(String name, String owner_id) {
        this.name = name;
        this.owner_id = owner_id;
    }

    /**
     * Returns the name of the facility.
     *
     * @return the name of the facility
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the facility.
     *
     * @param name the new name for the facility
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the owner ID of the facility.
     *
     * @return the owner ID of the facility
     */
    public String getOwner_id() {
        return owner_id;
    }
}
