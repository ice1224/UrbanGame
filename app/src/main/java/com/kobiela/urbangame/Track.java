package com.kobiela.urbangame;

import java.io.Serializable;
import java.util.List;

public class Track implements Serializable {
    private String title;
    private String description;
    private String location;
    private String author;
    private List<Riddle> riddles;
    private int qualityRateSum, difficultyRateSum, lengthRateSum;
    private int numberOfVotes;

    public Track(String title, String description, String location, String author, List<Riddle> riddles, int qualityRateSum, int difficultyRateSum, int lengthRateSum, int numberOfVotes) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.author = author;
        this.riddles = riddles;
        this.qualityRateSum = qualityRateSum;
        this.difficultyRateSum = difficultyRateSum;
        this.lengthRateSum = lengthRateSum;
        this.numberOfVotes = numberOfVotes;
    }

    public Track(String title, String description, String location, String author, int qualityRateSum, int difficultyRateSum, int lengthRateSum, int numberOfVotes) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.author = author;
        this.qualityRateSum = qualityRateSum;
        this.difficultyRateSum = difficultyRateSum;
        this.lengthRateSum = lengthRateSum;
        this.numberOfVotes = numberOfVotes;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Riddle> getRiddles() {
        return riddles;
    }

    public void setRiddles(List<Riddle> riddles) {
        this.riddles = riddles;
    }

    public int getQualityRateSum() {
        return qualityRateSum;
    }

    public void setQualityRateSum(int qualityRateSum) {
        this.qualityRateSum = qualityRateSum;
    }

    public int getDifficultyRateSum() {
        return difficultyRateSum;
    }

    public void setDifficultyRateSum(int difficultyRateSum) {
        this.difficultyRateSum = difficultyRateSum;
    }

    public int getLengthRateSum() {
        return lengthRateSum;
    }

    public void setLengthRateSum(int lengthRateSum) {
        this.lengthRateSum = lengthRateSum;
    }

    public int getNumberOfVotes() {
        return numberOfVotes;
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }

    public float getQualityAverage() {
        return Math.round((qualityRateSum * 1.0f / numberOfVotes) * 100.f) / 100.f;
    }

    public float getDifficultyAverage() {
        return Math.round((difficultyRateSum * 1.0f / numberOfVotes) * 100.f) / 100.f;
    }

    public float getLengthAverage() {
        return Math.round((lengthRateSum * 1.0f / numberOfVotes) * 100.f) / 100.f;
    }

}
