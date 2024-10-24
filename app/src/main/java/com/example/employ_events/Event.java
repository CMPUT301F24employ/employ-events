package com.example.employ_events;

import android.media.Image;

import java.sql.Time;
import java.util.Date;

//Update constructors...don't need all of them, can just set the stuff using getters and setters
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
    private Time endTime;
    private Time startTime;
    private Integer organizerID;
    public Event(String eventTitle, Date eventDate, Date registrationDateDeadline, Date registrationStartDate, Boolean geoLocation, String description, Time endTime, Time startTime){
        this.description = description;
        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.registrationStartDate = registrationStartDate;
        this.registrationDateDeadline = registrationDateDeadline;
        this.geoLocation = geoLocation;
        this.endTime = endTime;
        this.startTime = startTime;

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
}

