package com.example.employ_events.ui.facility;

/**
 * Represents a facility with a name and an owner ID.
 */
public class Facility {
    String name, organizer_id, email, address, phone_number;

    /**
     * Constructs a Facility with the specified name and owner ID.
     *
     * @param name the name of the facility
     * @param organizer_id the ID of the owner of the facility
     */
    public Facility(String name, String email, String address, String organizer_id) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.organizer_id = organizer_id;
    }

    public Facility(String name, String email, String address, String organizer_id, String phone_number) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone_number = phone_number;
        this.organizer_id = organizer_id;
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
    public String getOrganizer_id() {
        return organizer_id;
    }

    public void setOrganizer_id(String organizer_id) {
        this.organizer_id = organizer_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}

