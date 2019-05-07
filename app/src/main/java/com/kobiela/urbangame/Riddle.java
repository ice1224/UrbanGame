package com.kobiela.urbangame;

import com.google.android.gms.maps.model.LatLng;

public class Riddle {
    private LatLng coords;
    private String riddleText;

    public Riddle(LatLng coords, String riddleText) {
        this.coords = coords;
        this.riddleText = riddleText;
    }

    public Riddle(double lat, double lng, String riddleText){
        coords = new LatLng(lat,lng);
        this.riddleText = riddleText;
    }

    public Riddle(String lat, String lng, String riddleText){
        coords = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        this.riddleText = riddleText;
    }

    public LatLng getCoords() {
        return coords;
    }

    public String getRiddleText() {
        return riddleText;
    }
}
