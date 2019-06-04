package com.kobiela.urbangame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackSummaryActivity extends AppCompatActivity {

    private List<Riddle> game = new ArrayList<>();
    private Button bSaveTrack;
    private EditText etTrackTitle, etTrackDescription, etTrackLocation, etTrackAuthor;
    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection("tracksCollection");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_summary);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        handleGame();

        bSaveTrack = findViewById(R.id.b_save_track);
        etTrackTitle = findViewById(R.id.et_track_title);
        etTrackDescription = findViewById(R.id.et_track_description);
        etTrackLocation = findViewById(R.id.et_track_location);
        etTrackAuthor = findViewById(R.id.et_track_author);

        bSaveTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etTrackTitle.getText().toString().length() == 0) {
                    Toast.makeText(TrackSummaryActivity.this, "Title field is empty!", Toast.LENGTH_LONG).show();
                }
                else{
                    saveToFirebase();
                    Toast.makeText(TrackSummaryActivity.this, "Track saved!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TrackSummaryActivity.this, MapsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(MapsActivity.SHOULD_FINISH, true);
                    startActivity(intent);
                    TrackSummaryActivity.this.finish();
                }
            }
        });
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

    private void saveToFirebase() {
        String title = etTrackTitle.getText().toString();
        String description = etTrackDescription.getText().toString();
        String location = etTrackLocation.getText().toString();
        String author = etTrackAuthor.getText().toString();
        Map<String,Object> dataToSave = new HashMap<>();
        dataToSave.put("TITLE", title);
        dataToSave.put("DESCRIPTION", description);
        dataToSave.put("LOCATION", location);
        dataToSave.put("AUTHOR", author);
        mColRef.add(dataToSave)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        for (int i = 0; i < game.size(); i++) {
                            LatLng coords = game.get(i).getCoords();
                            String riddle = game.get(i).getRiddleText();

                            Map<String,Object> riddlesToSave = new HashMap<>();
                            riddlesToSave.put("COORDS", coords);
                            riddlesToSave.put("RIDDLE", riddle);

                            documentReference.collection("riddles").document(String.valueOf(i)).set(riddlesToSave);
                        }
                    }
                });
    }
}
