package com.example.travelhut.model.objects;

public class Notification {

    //Instance Variables
    private String userid;
    private boolean ispost;
    private String text;
    private String postid;

    //Constructors
    public Notification(String userid, boolean ispost, String text, String postid) {
        this.userid = userid;
        this.ispost = ispost;
        this.text = text;
        this.postid = postid;
    }

    public Notification(){

    }

    //Getters and Setters

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }

}
