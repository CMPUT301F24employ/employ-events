package com.example.employ_events.ui.entrants;

public class Entrant {
    private String name;
    private Boolean onWaitingList;
    private Boolean onCancelledList;
    private Boolean onAcceptedList;
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
}
