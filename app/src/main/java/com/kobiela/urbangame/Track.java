package com.kobiela.urbangame;

import java.util.List;

public class Track {
    private String title;
    private String description;
    private String location;
    private String author;
    private List<Riddle> riddles;

    public Track(String title, String description, String location, String author, List<Riddle> riddles) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.author = author;
        this.riddles = riddles;
    }

    public Track(String title, String description, String location, String author) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public List<Riddle> getRiddles() {
        return riddles;
    }

    public void setRiddles(List<Riddle> riddles) {
        this.riddles = riddles;
    }


}
