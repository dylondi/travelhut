package com.example.travelhut.model.objects;

public class Trip {

    //Instance Variables
    private String tripid;
    private String placeid;
    private String placename;
    private String placeaddress;
    private String daterange;
    private long startdate;
    private long enddate;


    //Constructors
    public Trip(String placename, String daterange, String placeaddress, String tripid, String placeid, long startdate, long enddate) {
        this.placename = placename;
        this.placeaddress = placeaddress;
        this.daterange = daterange;
        this.tripid = tripid;
        this.placeid = placeid;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public Trip() {
    }


    //Getters and Setters
    public long getStartdate() {
        return startdate;
    }

    public void setStartdate(long startdate) {
        this.startdate = startdate;
    }

    public long getEnddate() {
        return enddate;
    }

    public void setEnddate(long enddate) {
        this.enddate = enddate;
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
