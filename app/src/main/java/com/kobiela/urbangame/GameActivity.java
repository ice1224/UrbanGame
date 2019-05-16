package com.kobiela.urbangame;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.maps.UiSettings;
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
    private int numberMapType = 0;
    private List<Riddle> game = new ArrayList<>();
    private TextView tvRiddle;
    private TextView tvRiddleText;
    private int currentNumber = -1;
    private LatLng currentRiddleCoords;
    private static final int ROUNDING_ACC = 4;
    private static final int RANGE = 1;

    private static boolean isApplicationStopped = false;
    private static String CHANNEL_ID = "notification_channel_01";
    private Button bMapChange;
    private Button bMapInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        tvRiddle = findViewById(R.id.tv_riddle_title);
        tvRiddleText = findViewById(R.id.tv_riddle_text);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createNotificationChannel();

        handleButtons();
        handleGame();
        updateView();
        tvRiddleText.setText(game.get(currentNumber).getRiddleText());
        tvRiddleText.setMovementMethod(new ScrollingMovementMethod());

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

                                Toast.makeText(GameActivity.this,"TO FOUND:\n" +
                                                String.valueOf(currentRiddleCoords.latitude) + "   |   " + String.valueOf(currentRiddleCoords.longitude) + "\n" +
                                                "CURRENT:\n" +
                                                String.valueOf(round(location.getLatitude())) + "   |   " + String.valueOf(round(location.getLongitude())),
                                        Toast.LENGTH_LONG).show();


                                    if (currentNumber < game.size() && isPositionInRange(location.getLatitude(), location.getLongitude())) {
                                        if(isApplicationStopped){
                                            sendNotification();
                                        }
                                        else {
                                            openDialogWindow();
                                        }
                                        updateView();
                                    }
                                }
                            }
                        });
            }
        };
        startLocationUpdates();
    }

    private void handleButtons(){
        bMapInfo = findViewById(R.id.b_map_info);
        bMapInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(GameActivity.this);
                dialog.setContentView(R.layout.popup_info_maps);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView iv_close_info = dialog.findViewById(R.id.ic_ma_close_info);
                TextView tv_game_info_first = dialog.findViewById(R.id.tv_info_map_first);
                tv_game_info_first.setText(R.string.tv_ga_pi_first);

                iv_close_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        bMapChange = findViewById(R.id.b_map_change);
        bMapChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMapType();
            }
        });
    }

    private boolean isPositionInRange(double currentLatitude, double currentLongitude){
        double latDif = Math.abs(round(currentLatitude) * Math.pow(10,ROUNDING_ACC) - currentRiddleCoords.latitude * Math.pow(10,ROUNDING_ACC));
        double lngDif = Math.abs(round(currentLongitude) * Math.pow(10,ROUNDING_ACC) - currentRiddleCoords.longitude * Math.pow(10,ROUNDING_ACC));
        return (latDif <= RANGE && lngDif <= RANGE);
    }

    private void openDialogWindow(){
        final Dialog dialog = new Dialog(GameActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_success);
        Button btSuccessOk = dialog.findViewById(R.id.b_ok_dialog_success);
        btSuccessOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(currentNumber == game.size()){
                    GameActivity.this.finish();
                }
            }
        });
        dialog.show();
    }

    private void sendNotification(){
        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(GameActivity.this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(GameActivity.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_star_black_24dp)
                .setContentTitle("Gratulacje")
                .setContentText("Dotarłeś w dobre miejsce")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GameActivity.this);
        notificationManager.notify(1, builder.build());
        Toast.makeText(GameActivity.this, "STOPPED", Toast.LENGTH_LONG).show();
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
        if(currentNumber < game.size()) {
            if (currentNumber >= 0) {
                mMap.addMarker(new MarkerOptions()
                        .position(game.get(currentNumber).getCoords())
                        .title(String.valueOf(currentNumber+1))
                        .snippet(game.get(currentNumber).getRiddleText())
                );
            }
            ++currentNumber;
            System.out.println(currentNumber);
            if(currentNumber < game.size()) {
                currentRiddleCoords = new LatLng(
                        round(game.get(currentNumber).getCoords().latitude),
                        round(game.get(currentNumber).getCoords().longitude));
                tvRiddleText.setText(game.get(currentNumber).getRiddleText());
                tvRiddle.setText(getString(R.string.tv_ga_riddle,currentNumber+1, game.size()));
            }
        }
        else{
            stopLocationUpdates();
            openDialogWindow();
        }
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isApplicationStopped = false;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isApplicationStopped = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.night_theme));
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);

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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        /**
         * name i description powinny mieć getString(R.string.channel_name); itp/
         * **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotificationsChannel";
            String description = "Channel to send notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void changeMapType() {
        switch (numberMapType){
            case 0: {
                mMap.setMapStyle(new MapStyleOptions("[]"));
                break;
            }
            case 1: {
                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.retro_theme));
                break;
            }
            case 2: mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); break;
            case 3: mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); break;
            case 4: mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN); break;
            case 5: {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.night_theme));
                break;
            }
        }
        numberMapType++;
        if(numberMapType>5) numberMapType = 0;
    }

}