package com.example.travelhut.model.objects;

public class Weather {

    //Instance Variables
    private String time;
    private Long temp;
    private String humidity;
    private String imgUrl;

    //Constructor
    public Weather(String time, Long temp, String humidity, String imgUrl) {
        this.time = time;
        this.temp = temp;
        this.humidity = humidity;
        this.imgUrl = imgUrl;
    }

    //Getters and Setters
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getTemp() {
        return temp;
    }

    public void setTemp(Long temp) {
        this.temp = temp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
