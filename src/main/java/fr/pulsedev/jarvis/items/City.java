package fr.pulsedev.jarvis.items;

/**
 * This file is a part of VoiceAssistant, located on fr.renardfute.jarvis.items
 * Copyright (c) Renard - All rights reserved
 *
 * @author Renard
 * Created the 28/07/2020 at 12:51.
 */
public class City {
    public long id;
    public String name;
    public String state;
    public String country;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City(long id, String name, String state, String country) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.country = country;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }
}
