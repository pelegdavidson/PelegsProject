package com.example.peleg.pelegsproject;


import java.util.ArrayList;
import java.util.GregorianCalendar;

public class Event {
    String eventName;
    String owner; //uid
    Double locLatitude;
    Double locLongitude;
    long duration;
    ArrayList<String> members;//uids
    boolean isCyclic;
    long date;

    public Event() {
    }

    public Event(String owner, String eventName, Double locLatitude, Double locLongitude, long duration, ArrayList<String> members, boolean isCyclic, long date) {
        this.owner = owner;
        this.eventName = eventName;
        this.locLatitude = locLatitude;
        this.locLongitude = locLongitude;
        this.duration = duration;
        this.members = members;
        this.isCyclic = isCyclic;
        this.date = date;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Double getLocLatitude() {
        return locLatitude;
    }

    public void setLocLatitude(Double locLatitude) {
        this.locLatitude = locLatitude;
    }

    public Double getLocLongitude() {
        return locLongitude;
    }

    public void setLocLongitude(Double locLongitude) {
        this.locLongitude = locLongitude;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public boolean isCyclic() {
        return isCyclic;
    }

    public void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;
    }

    public long getDate() {
        return date ;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
