package com.example.employ_events.ui.events;

import android.media.Image;

import com.example.employ_events.ui.entrants.Entrant;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;




//Update constructors...don't need all of them, can just set the stuff using getters and setters
/**
 * Represents an event with various attributes such as title, date, registration deadlines, and more.
 */
public class Event {
    //Constructors -> may not need all of them depending on AddEventFragment implementation
    private String id;
    private String eventTitle;
    private Date eventDate;
    private Date registrationDateDeadline;
    private Date registrationStartDate;
    private Boolean geoLocation;
    private String description;
    private Integer limited;
    private Time endTime;
    private Time startTime;
    private String organizerID;
    private Integer fee;
    private Integer eventCapacity;
    private String facilityID;
    private String bannerUri;
    private ArrayList<Entrant> entrantsList = new ArrayList<>();

    /**
     * Empty constructor for displaying only some details of an event.
     */
    public Event() {

    }

    /**
     * Constructs an Event with the specified details.
     *
     * @param id                     The unique identifier for the event.
     * @param eventTitle             The title of the event.
     * @param eventDate              The date of the event.
     * @param registrationDateDeadline The registration deadline date.
     * @param registrationStartDate   The registration start date.
     * @param geoLocation            Indicates if geolocation is enabled.
     * @param organizerID            The ID of the organizer.
     */
    public Event(String id, String eventTitle, Date eventDate, Date registrationDateDeadline, Date registrationStartDate, Boolean geoLocation,   String organizerID, Integer eventCapacity){

        this.id = id;
        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.registrationStartDate = registrationStartDate;
        this.registrationDateDeadline = registrationDateDeadline;
        this.geoLocation = geoLocation;
        this.organizerID = organizerID;
        this.eventCapacity = eventCapacity;
    }

    //Getters and setters
    // Getter and setter for the ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and setter for bannerUrl
    public String getBannerUri() {
        return bannerUri;
    }

    public void setBannerUri(String bannerUri) {
        this.bannerUri = bannerUri;
    }

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

    public Integer getEventCapacity() {
        return eventCapacity;
    }

    public void setEventCapacity(Integer eventCapacity) {
        this.eventCapacity = eventCapacity;
    }
    public Boolean addEntrant(Entrant entrant){
        if (this.limited != null){
            if (entrantsList.size() < this.limited){
                entrantsList.add(entrant);
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } else{
          entrantsList.add(entrant);
          return Boolean.TRUE;
        }
    }

    public void generateSample(){
        Random random = new Random();
        ArrayList<Integer> randomlyGeneratedNumbers = new ArrayList<Integer>();
        Integer i = 0;
        Integer sampled;
        Integer capOfSample = Math.min(this.eventCapacity, this.entrantsList.size());
        while (i < capOfSample){
            sampled = random.nextInt(capOfSample);
            if (randomlyGeneratedNumbers.contains(sampled)){
                continue;
            }
            randomlyGeneratedNumbers.add(sampled);
            i +=1;
        }
        Entrant selected;
        Integer k;
        Integer selectedIndex;
        for (k =0; k<randomlyGeneratedNumbers.size(); k++){
            selectedIndex = randomlyGeneratedNumbers.get(k);
            selected = entrantsList.get(selectedIndex);
            if (selected.getOnCancelledList() == Boolean.TRUE || selected.getOnWaitingList() == Boolean.FALSE){
                continue;
            }
            selected.setOnAcceptedList(Boolean.TRUE);
        }


    }

}