package com.example.travelhut.model.objects;

public class CovidStatistics {

    //Instance Variables
    private String recommendation;
    private String description;
    private String masks;
    private String quarantine;
    private String tests;

    //Constructor
    public CovidStatistics(String recommendation, String description, String masks, String quarantine, String tests) {
        this.recommendation = recommendation;
        this.description = description;
        this.masks = masks;
        this.quarantine = quarantine;
        this.tests = tests;
    }


    //Getters and Setters
    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMasks() {
        return masks;
    }

    public void setMasks(String masks) {
        this.masks = masks;
    }

    public String getQuarantine() {
        return quarantine;
    }

    public void setQuarantine(String quarantine) {
        this.quarantine = quarantine;
    }

    public String getTests() {
        return tests;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }
}
