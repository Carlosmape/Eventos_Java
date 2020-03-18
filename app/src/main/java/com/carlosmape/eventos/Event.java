package com.carlosmape.eventos;

public class Event {
    private String event;
    private String city;
    private String date;
    private String image;

    public Event(String event, String city, String date, String image) {
        this.event = event;
        this.city = city;
        this.date = date;
        this.image = image;
    }
    public Event(){}

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
