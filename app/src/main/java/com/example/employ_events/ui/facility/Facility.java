package com.example.employ_events.ui.facility;

/**
 * Represents a facility with various attributes including name, owner ID, email, address,
 * phone number, and a profile picture URI.
 */
public class Facility {
    String name, organizer_id, email, address, phone_number, facilityPfpUri;

    /**
     * Constructs a Facility with the specified name, email, address, and owner ID.
     * @param name         the name of the facility
     * @param email        the email associated with the facility
     * @param address      the physical address of the facility
     * @param organizer_id  the ID of the owner of the facility
     */
    public Facility(String name, String email, String address, String organizer_id) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.organizer_id = organizer_id;
    }

    /**
     * Constructs a Facility with the specified name, email, address, owner ID, and phone number.
     * @param name         the name of the facility
     * @param email        the email associated with the facility
     * @param address      the physical address of the facility
     * @param organizer_id  the ID of the owner of the facility
     * @param phone_number  the phone number for the facility
     */
    public Facility(String name, String email, String address, String organizer_id, String phone_number) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone_number = phone_number;
        this.organizer_id = organizer_id;
    }

    /**
     * Returns the name of the facility.
     * @return the name of the facility
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the facility.
     * @param name the new name for the facility
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the owner ID of the facility.
     * @return the owner ID of the facility
     */
    public String getOrganizer_id() {
        return organizer_id;
    }

    /**
     * Sets the ID of the owner of the facility.
     * @param organizer_id the new owner ID for the facility
     */
    public void setOrganizer_id(String organizer_id) { this.organizer_id = organizer_id; }

    /**
     * Returns the email associated with the facility.
     * @return the email of the facility
     */
    public String getEmail() { return email; }

    /**
     * Sets the email associated with the facility.
     * @param email the new email for the facility
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Returns the physical address of the facility.
     * @return the address of the facility
     */
    public String getAddress() { return address; }

    /**
     * Sets the physical address of the facility.
     * @param address the new address for the facility
     */
    public void setAddress(String address) { this.address = address; }

    /**
     * Returns the phone number associated with the facility.
     * @return the phone number of the facility, or null if not set
     */
    public String getPhone_number() { return phone_number; }

    /**
     * Sets the phone number associated with the facility.
     * @param phone_number the new phone number for the facility
     */
    public void setPhone_number(String phone_number) { this.phone_number = phone_number; }

    /**
     * Returns the URI of the facility's profile picture.
     * @return the URI of the profile picture, or null if not set
     */
    public String getFacilityPfpUri() { return facilityPfpUri; }

    /**
     * Sets the URI of the facility's profile picture.
     * @param facilityPfpUri the new URI for the facility's profile picture
     */
    public void setFacilityPfpUri(String facilityPfpUri) { this.facilityPfpUri = facilityPfpUri; }
}

