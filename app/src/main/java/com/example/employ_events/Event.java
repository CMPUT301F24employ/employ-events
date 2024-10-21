package com.example.employ_events;

import android.media.Image;

import java.util.Date;

public class Event {
    //Constructors -> may not need all of them depending on AddEventFragment implementation
    private String eventTitle;
    private Date eventDate;
    private Date registrationDateDeadline;
    private Date registrationStartDate;
    private Boolean geoLocation;
    private String description;
    private Image image;
    private Integer limited;
    public Event(String eventTitle, Date eventDate, Date registrationDateDeadline,  Date registrationStartDate, Boolean geoLocation, String description){
        this.description = description;
        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.registrationStartDate = registrationStartDate;
        this.registrationDateDeadline = registrationDateDeadline;
        this.geoLocation = geoLocation;

    }
    public Event(String eventTitle, Date eventDate, Date registrationDateDeadline,  Date registrationStartDate, Boolean geoLocation, String description, Integer limited){
        this.description = description;
        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.registrationStartDate = registrationStartDate;
        this.registrationDateDeadline = registrationDateDeadline;
        this.geoLocation = geoLocation;
        this.limited = limited;
    }
    public Event(String eventTitle, Date eventDate, Date registrationDateDeadline,  Date registrationStartDate, Integer fee, Boolean geoLocation, String description, Image eventImage){
        this.description = description;
        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.registrationStartDate = registrationStartDate;
        this.registrationDateDeadline = registrationDateDeadline;
        this.geoLocation = geoLocation;
        this.image = eventImage;
    }
    public Event(String eventTitle, Date eventDate, Date registrationDateDeadline,  Date registrationStartDate, Integer fee, Boolean geoLocation, String description, Image eventImage, Integer limited){
        this.description = description;
        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.registrationStartDate = registrationStartDate;
        this.registrationDateDeadline = registrationDateDeadline;
        this.geoLocation = geoLocation;
        this.image = eventImage;
        this.limited = limited;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Integer getLimited() {
        return limited;
    }

    public void setLimited(Integer limited) {
        this.limited = limited;
    }
}

