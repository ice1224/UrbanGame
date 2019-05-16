package com.kobiela.urbangame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String SHOULD_FINISH = "SHOULD_FINISH";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private List<Marker> markers = new ArrayList<>();
    private List<String> riddles = new ArrayList<>();
    private int numberMapType = 0;

    private boolean buttonClicked = false;

    private Button bMapChange;
    private Button bMapInfo;
    private Button bMapSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

       handleButtons();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (getIntent().getBooleanExtra(SHOULD_FINISH, false)) {
            finish();
        }
    }

    private void handleButtons(){
        bMapChange = findViewById(R.id.b_map_change);
        bMapChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.changeMapType(MapsActivity.this, numberMapType, mMap);
                numberMapType = ++numberMapType%6;
            }
        });

        bMapInfo = findViewById(R.id.b_map_info);
        bMapInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MapsActivity.this);
                dialog.setContentView(R.layout.popup_info_maps);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView iv_close_info = dialog.findViewById(R.id.ic_ma_close_info);
                iv_close_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        bMapSave = findViewById(R.id.b_save_map);
        bMapSave.setEnabled(false);
        bMapSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndSummarize();
            }
        });

    }



    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_theme));
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng firstLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(firstLoc));
                        }
                    }
                });



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        );
                openDialogWindow(marker,true);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                openDialogWindow(marker, false);
                return false;
            }
        });

    }

    private void openDialogWindow(final Marker marker, final boolean isMarkerNew) {

        buttonClicked = false;
        final int ind = markers.indexOf(marker);
        final int numberOfAll = markers.size();

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_addedit_marker);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView iv_icon = dialog.findViewById(R.id.iv_icon_big_add_marker);
        ImageView iv_clear = dialog.findViewById(R.id.iv_clear_ev);
        TextView tv_new_edit_marker = dialog.findViewById(R.id.tv_new_marker);
        final EditText et_new_edit_riddle = dialog.findViewById(R.id.et_add_riddle);
        TextView tv_number_of_markers = dialog.findViewById(R.id.tv_number_of_markers);
        Button b_remove_riddle = dialog.findViewById(R.id.b_remove_riddle);
        Button b_add_riddle = dialog.findViewById(R.id.b_add_riddle);

        System.out.println((tv_new_edit_marker == null) + " | " + (et_new_edit_riddle == null) + " | " + (tv_number_of_markers == null));

        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_new_edit_riddle.getText().clear();
            }
        });

        et_new_edit_riddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_new_edit_riddle.setHint("");
            }
        });


        if(isMarkerNew){
            iv_icon.setImageResource(R.drawable.ic_add_location_black_24dp);
            tv_new_edit_marker.setText(R.string.tv_ma_new_marker);
            tv_number_of_markers.setText(getString(R.string.tv_ma_which_marker, numberOfAll+1, numberOfAll+1));
        }
        else {
            iv_icon.setImageResource(R.drawable.ic_edit_location_black_24dp);
            tv_new_edit_marker.setText(R.string.tv_ma_edit_marker);
            tv_number_of_markers.setText(getString(R.string.tv_ma_which_marker, ind+1, numberOfAll));
            et_new_edit_riddle.setText(riddles.get(ind));
        }


        b_add_riddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMarkerNew) {
                    riddles.add(et_new_edit_riddle.getText().toString());
                    markers.add(marker);
                }
                else{
                    riddles.set(ind,et_new_edit_riddle.getText().toString());
                }
                buttonClicked = true;
                if(markers.size()>0) {
                    bMapSave.setEnabled(true);
                }
                dialog.cancel();
            }
        });

        b_remove_riddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.remove();
                if(!isMarkerNew) {
                    markers.remove(ind);
                    riddles.remove(ind);
                    if(markers.size()==0) {
                        bMapSave.setEnabled(false);
                    }
                }
                buttonClicked = true;
                dialog.cancel();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(!buttonClicked && isMarkerNew) {
                    marker.remove();
                }
            }
        });
    }

    public void saveAndSummarize() {
        ArrayList<String> game = new ArrayList<>();
        for (int j = 0; j < markers.size(); j++) {
            game.add(String.valueOf(markers.get(j).getPosition().latitude));
            game.add(String.valueOf(markers.get(j).getPosition().longitude));
            game.add(riddles.get(j));
        }

        Intent intent = new Intent(this, TrackSummaryActivity.class);
        intent.putStringArrayListExtra("GAME", game);
        startActivity(intent);
    }
}
