package com.example.travelhut.model.objects;

public class Comment {

    //Instance Variables
    private String comment;
    private String author;

    //Constructors
    public Comment(String comment, String author) {
        this.comment = comment;
        this.author = author;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
