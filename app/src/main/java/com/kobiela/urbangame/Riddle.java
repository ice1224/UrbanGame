package com.kobiela.urbangame;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Riddle implements Serializable {
    private double lat;
    private double lng;
    private String riddleText;

    public Riddle(LatLng coords, String riddleText) {
        lat = coords.latitude;
        lat = coords.longitude;
        this.riddleText = riddleText;
    }

    public Riddle(double lat, double lng, String riddleText){
        this.lat = lat;
        this.lng = lng;
        this.riddleText = riddleText;
    }

    public Riddle(String lat, String lng, String riddleText){
        this.lat = Double.valueOf(lat);
        this.lng = Double.valueOf(lng);
        this.riddleText = riddleText;
    }

    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }

    public String getRiddleText() {
        return riddleText;
    }
}
