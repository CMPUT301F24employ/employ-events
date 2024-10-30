package com.example.employ_events.ui.events;

import android.media.Image;

import java.sql.Time;
import java.util.Date;


//Update constructors...don't need all of them, can just set the stuff using getters and setters
/**
 * Represents an event with various attributes such as title, date, registration deadlines, and more.
 */
public class Event {
    //Constructors -> may not need all of them depending on AddEventFragment implementation
    private String eventTitle;
    private Date eventDate;
    private Date registrationDateDeadline;
    private Date registrationStartDate;
    private Boolean geoLocation;
    private String description;
    private String imageUri;
    private Integer limited;
    private Time endTime;
    private Time startTime;
    private String organizerID;
    private Integer fee;
    private String facilityID;

    /**
     * Empty constructor for displaying only some details of an event.
     */
    public Event() {

    }

    /**
     * Constructs an Event with the specified details.
     *
     * @param eventTitle             The title of the event.
     * @param eventDate              The date of the event.
     * @param registrationDateDeadline The registration deadline date.
     * @param registrationStartDate   The registration start date.
     * @param geoLocation            Indicates if geolocation is enabled.
     * @param organizerID            The ID of the organizer.
     */
    public Event(String eventTitle, Date eventDate, Date registrationDateDeadline, Date registrationStartDate, Boolean geoLocation,   String organizerID){

        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.registrationStartDate = registrationStartDate;
        this.registrationDateDeadline = registrationDateDeadline;
        this.geoLocation = geoLocation;
        this.organizerID = organizerID;
    }

    //Getters and setters

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getRegistrationDateDeadline() {
        return registrationDateDeadline;
    }

    public void setRegistrationDateDeadline(Date registrationDateDeadline) {
        this.registrationDateDeadline = registrationDateDeadline;
    }

    public Date getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(Date registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public Boolean getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(Boolean geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return imageUri;
    }

    public void setImage(String imageURI) {
        this.imageUri = imageURI;
    }

    public Integer getLimited() {
        return limited;
    }

    public void setLimited(Integer limited) {
        this.limited = limited;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public String getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }
}