package com.example.travelhut.model.objects;

public class User {
    private String id;
    private String email;
    private String username;
    private String displayname;
    private String imageurl;
    private String bio;
    private String url;

    public User(String id, String username, String displayname, String bio, String email, String imageurl, String url) {
        this.id = id;
        this.email = email;
        this.displayname = displayname;
        this.bio = bio;
        this.username = username;
        this.imageurl = imageurl;
        this.url = url;
    }

    public User() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
