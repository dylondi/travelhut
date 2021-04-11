package com.example.travelhut.model.objects;

public class Event {

    //Instance Variables
    private String name;
    private String venue;
    private String date;
    private String eventid;
    private String imageUrl;

    //Constructor
    public Event(String name, String venue, String date, String eventid,String imageUrl) {
        this.name = name;
        this.venue = venue;
        this.date = date;
        this.eventid = eventid;
        this.imageUrl = imageUrl;
    }

    //Getters and Setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }
}
