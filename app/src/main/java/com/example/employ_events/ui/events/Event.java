package com.example.employ_events.ui.events;

import android.util.Log;

import com.example.employ_events.ui.entrants.Entrant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Represents an event with various attributes such as title, date, registration deadlines,
 * fees, and capacity. It can manage a list of entrants and determine selections from that list.
 */
public class Event {
    //Constructors -> may not need all of them depending on AddEventFragment implementation
    private String id, eventTitle, description, facilityID, bannerUri;
    private Date eventDate, registrationDateDeadline, registrationStartDate;
    private Boolean geoLocation;
    private Integer fee, eventCapacity, limited;
    private ArrayList<Entrant> entrantsList = new ArrayList<>();
    private FirebaseFirestore db;

    /**
     * Default constructor for creating an event without details.
     * This constructor can be useful for displaying only some details of an event.
     */
    public Event() { this.db = FirebaseFirestore.getInstance(); }

    /**
     * Constructs an Event with the specified details.
     *
     * @param eventTitle             The title of the event.
     * @param eventDate              The date when the event will occur.
     * @param registrationDateDeadline The date by which registration must be completed.
     * @param registrationStartDate   The date when registration opens.
     * @param geoLocation            Indicates whether geolocation features are enabled.
     * @param organizerID            The ID of the facility organizing the event.
     * @param eventCapacity          The maximum number of entrants allowed for the event.
     */
    public Event(String eventTitle, String organizerID,
                 Date eventDate, Date registrationDateDeadline, Date registrationStartDate,
                 Boolean geoLocation, Integer eventCapacity){
        //this.id = id;
        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.registrationStartDate = registrationStartDate;
        this.registrationDateDeadline = registrationDateDeadline;
        this.geoLocation = geoLocation;
        this.facilityID = organizerID;
        this.eventCapacity = eventCapacity;
        this.db = FirebaseFirestore.getInstance();

    }

    /**
     * Returns the unique identifier for the event.
     * @return the unique ID of the event
     */
    public String getId() { return id; }

    /**
     * Sets the unique identifier for the event.
     * @param id the unique ID to set for the event
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the URI of the event's banner image.
     * @return the banner URI, or null if not set
     */
    public String getBannerUri() {
        return bannerUri;
    }

    /**
     * Sets the URI of the event's banner image.
     * @param bannerUri the new banner URI to set for the event
     */
    public void setBannerUri(String bannerUri) {
        this.bannerUri = bannerUri;
    }

    /**
     * Returns the title of the event.
     * @return the event title
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /**
     * Sets the title of the event.
     * @param eventTitle the new title for the event
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * Returns the date of the event.
     * @return the event date
     */
    public Date getEventDate() {
        return eventDate;
    }

    /**
     * Sets the date of the event.
     * @param eventDate the new date for the event
     */
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * Returns the registration deadline date for the event.
     * @return the registration deadline date
     */
    public Date getRegistrationDateDeadline() { return registrationDateDeadline; }

    /**
     * Sets the registration deadline date for the event.
     * @param registrationDateDeadline the new registration deadline date
     */
    public void setRegistrationDateDeadline(Date registrationDateDeadline) {
        this.registrationDateDeadline = registrationDateDeadline; }

    /**
     * Returns the start date for event registration.
     * @return the registration start date
     */
    public Date getRegistrationStartDate() { return registrationStartDate; }

    /**
     * Sets the start date for event registration.
     * @param registrationStartDate the new registration start date
     */
    public void setRegistrationStartDate(Date registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    /**
     * Returns whether geolocation features are enabled for the event.
     * @return true if geolocation is enabled, false otherwise
     */
    public Boolean getGeoLocation() {
        return geoLocation;
    }

    /**
     * Sets whether geolocation features are enabled for the event.
     * @param geoLocation true to enable geolocation, false to disable
     */
    public void setGeoLocation(Boolean geoLocation) {
        this.geoLocation = geoLocation;
    }

    /**
     * Returns the description of the event.
     * @return the event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event.
     * @param description the new description for the event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the maximum number of entrants allowed on the waiting list for the event.
     * @return the waiting list capacity, or null if not limited
     */
    public Integer getLimited() {
        return limited;
    }

    /**
     * Sets the maximum number of entrants allowed on the waiting list for the event.
     * @param limited the new waiting list capacity; set to null for no limit
     */
    public void setLimited(Integer limited) {
        this.limited = limited;
    }

    /**
     * Returns the registration fee for the event.
     * @return the event registration fee, or null if not set
     */
    public Integer getFee() {
        return fee;
    }

    /**
     * Sets the registration fee for the event.
     * @param fee the new registration fee to set for the event
     */
    public void setFee(Integer fee) {
        this.fee = fee;
    }

    /**
     * Returns the ID of the facility organizing the event.
     * @return the facility ID
     */
    public String getFacilityID() {
        return facilityID;
    }

    /**
     * Sets the ID of the facility organizing the event.
     * @param facilityID the new facility ID to set for the event
     */
    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }

    /**
     * Returns the maximum number of entrants allowed for the event.
     * @return the event capacity
     */
    public Integer getEventCapacity() {
        return eventCapacity;
    }

    /**
     * Sets the maximum number of entrants allowed for the event.
     * @param eventCapacity the new event capacity
     */
    public void setEventCapacity(Integer eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    /**
     * Adds an entrant to the waiting list if waiting list capacity allows.
     *
     * @param entrant the Entrant object to be added
     * @return true if the entrant was successfully added, false if the waiting list is at capacity
     */
    public boolean addEntrant(Entrant entrant) {
        // Check if there is a capacity limit
        if (this.limited != null) {
            // If the number of entrants is less than the limit, add the entrant
            if (entrantsList.size() < this.limited) {
                entrantsList.add(entrant);
                entrant.setOnWaitingList(true); // true is the same as Boolean.TRUE
                return true;
            } else {
                // Capacity is full
                return false;
            }
        } else {
            // No limit, just add the entrant
            entrantsList.add(entrant);
            entrant.setOnWaitingList(true);
            return true;
        }
    }

    public void setEntrantsList(ArrayList<Entrant> entrantsList) {
        this.entrantsList = entrantsList;
    }

    /**
     * Returns the list of entrants for the event.
     * @return the list of entrants
     */
    public ArrayList<Entrant> getEntrantsList() {
        return entrantsList;
    }

    /**
     * Generates a random sample of entrants from the entrants list based on the event's capacity.
     * Entrants who are cancelled or not on the waiting list will be excluded from selection.
     */
    public void generateSample() {
        Random random = new Random();
        ArrayList<Integer> randomlyGeneratedNumbers = new ArrayList<>();
        Integer capOfSample = Math.min(this.eventCapacity, this.entrantsList.size());
        Integer i = 0;

        // Randomly select entrants based on event capacity
        while (i < capOfSample) {
            int sampled = random.nextInt(this.entrantsList.size());
            if (!randomlyGeneratedNumbers.contains(sampled)) {
                randomlyGeneratedNumbers.add(sampled);
                i++;
            }
        }

        // Mark selected entrants as accepted
        for (Integer index : randomlyGeneratedNumbers) {
            Entrant selected = entrantsList.get(index);
            if (selected.getOnCancelledList() || !selected.getOnWaitingList()) {
                continue;
            }
            selected.setOnAcceptedList(true);
            selected.setOnWaitingList(false);

            // Update the specific entrant's document in the subcollection
            String uniqueID = selected.getUniqueID();
            Map<String, Object> data = new HashMap<>();
            data.put("onAcceptedList", true);
            data.put("onWaitingList", false);

            db.collection("events")
                    .document(getId())
                    .collection("entrantsList")
                    .document(uniqueID)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("generateSample", "Updated entrant " + uniqueID))
                    .addOnFailureListener(e -> Log.e("generateSample", "Error updating entrant " + uniqueID, e));
        }
    }


}