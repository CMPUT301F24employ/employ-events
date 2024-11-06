package com.example.employ_events.ui.entrants;

import java.util.HashMap;
import java.util.Map;

public class Entrant {
    private String name;
    private Boolean onWaitingList;
    private Boolean onCancelledList;
    private Boolean onAcceptedList;
    private Boolean onRegisteredList;
    public Entrant() {

    }

    public Entrant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOnWaitingList() {
        return onWaitingList;
    }

    public void setOnWaitingList(Boolean onWaitingList) {
        this.onWaitingList = onWaitingList;
    }

    public Boolean getOnCancelledList() {
        return onCancelledList;
    }

    public void setOnCancelledList(Boolean onCancelledList) {
        this.onCancelledList = onCancelledList;
    }

    public Boolean getOnAcceptedList() {
        return onAcceptedList;
    }

    public void setOnAcceptedList(Boolean onAcceptedList) {
        this.onAcceptedList = onAcceptedList;
    }

    public Boolean getOnRegisteredList() {
        return onRegisteredList;
    }

    public void setOnRegisteredList(Boolean onRegisteredList) {
        this.onRegisteredList = onRegisteredList;
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", this.name); // Replace with actual fields
        map.put("onAcceptedList", this.getOnAcceptedList());
        map.put("onCancelledList", this.getOnCancelledList());
        map.put("onWaitingList", this.getOnWaitingList());
        // Add other fields as needed
        return map;
    }
}
