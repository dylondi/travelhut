package com.example.travelhut.views.main.trips.trip_fragments;

public class Trip {

    private String tripid;
    private String placeid;
    private String placename;
    private String placeaddress;
    private String daterange;

    public Trip(String placename, String daterange, String placeaddress, String tripid, String placeid) {
        this.placename = placename;
        this.placeaddress = placeaddress;
        this.daterange = daterange;
        this.tripid = tripid;
        this.placeid = placeid;
    }

    public Trip() {
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getTripid() {
        return tripid;
    }

    public void setTripid(String tripid) {
        this.tripid = tripid;
    }

    public String getPlaceaddress() {
        return placeaddress;
    }

    public void setPlaceaddress(String placeaddress) {
        this.placeaddress = placeaddress;
    }

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public String getDaterange() {
        return daterange;
    }

    public void setDaterange(String daterange) {
        this.daterange = daterange;
    }
}
