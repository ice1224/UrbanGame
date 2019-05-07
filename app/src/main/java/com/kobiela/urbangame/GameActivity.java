package com.kobiela.urbangame;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean requestingLocationUpdates = false;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private List<Riddle> game = new ArrayList<>();
    private TextView tvRiddleText;
    private int currentNumber = -1;
    private LatLng currentCoords;
    private static final int ROUNDING_ACC = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        tvRiddleText = findViewById(R.id.tvRiddleText);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        handleGame();
        updateView();
        tvRiddleText.setText(game.get(currentNumber).getRiddleText());

        createLocationRequest();
        locationCallback = new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(GameActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {



                                    Toast.makeText(GameActivity.this,
                                    "TO FOUND:\n" +
                                    String.valueOf(currentCoords.latitude) + "   |   " + String.valueOf(currentCoords.longitude) + "\n" +
                                    "CURRENT:\n" +
                                    String.valueOf(round(location.getLatitude())) + "   |   " + String.valueOf(round(location.getLongitude())),
                                    Toast.LENGTH_LONG).show();

                                    if(currentCoords.latitude == round(location.getLatitude()) &&
                                       currentCoords.longitude == round(location.getLongitude())){

                                            Dialog dialog = new Dialog(GameActivity.this);
                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            dialog.setContentView(R.layout.popup_success);
                                            dialog.show();
                                            updateView();
                                    }
                                }
                            }
                        });
            }
        };
        startLocationUpdates();

    }

    private void handleGame() {
        Intent intent = getIntent();
        ArrayList<String> gameAL = intent.getStringArrayListExtra("GAME");

        for (int i = 0; i <= gameAL.size()-3; i=i+3) {
            System.out.print(gameAL.get(i) + "   |   ");
            System.out.print(gameAL.get(i+1) + "   |   ");
            System.out.print(gameAL.get(i+2));
            System.out.println();
            game.add(new Riddle(
                    gameAL.get(i),
                    gameAL.get(i+1),
                    gameAL.get(i+2)));
        }
    }

    private void updateView(){
        if(currentNumber<game.size()) {
            if (currentNumber >= 0) {
                mMap.addMarker(new MarkerOptions()
                        .position(game.get(currentNumber).getCoords())
                        .title(String.valueOf(currentNumber)+1)
                );
            }
            ++currentNumber;
            System.out.println(currentNumber);
            currentCoords = new LatLng(
                    round(game.get(currentNumber).getCoords().latitude),
                    round(game.get(currentNumber).getCoords().longitude));
            tvRiddleText.setText(game.get(currentNumber).getRiddleText());
        }
        else{

        }
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.night_theme));


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng firstLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(firstLoc));
                        }
                    }
                });
    }

    private static double round(double value) {
        if (ROUNDING_ACC < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(ROUNDING_ACC, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
