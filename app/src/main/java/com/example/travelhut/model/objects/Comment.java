package com.example.travelhut.model.objects;

public class Comment {

    //Instance Variables
    private String comment;
    private String publisher;

    //Constructors
    public Comment(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }
    public Comment() {
    }

    //Getters and Setters
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
