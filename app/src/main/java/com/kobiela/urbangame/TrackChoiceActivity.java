package com.kobiela.urbangame;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrackChoiceActivity extends AppCompatActivity {

    private static final String TAG = "TrackChoiceActivity";
    private List<String> idsList = new ArrayList<>();
    private List<Track> trackList = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_choice);

        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        readData();
    }

    private void readData() {
        FirebaseFirestore.getInstance().collection("tracksCollection")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                               // Toast.makeText(TrackChoiceActivity.this,document.getId() + " => " + document.getData(), Toast.LENGTH_LONG).show();
                                String title = document.get("TITLE").toString();
                                String description = document.get("DESCRIPTION").toString();
                                String location = document.get("LOCATION").toString();
                                trackList.add(new Track(title,description,location));
                                idsList.add(document.getId());
                            }
                            Toast.makeText(TrackChoiceActivity.this, trackList.size() + " " + idsList.size(), Toast.LENGTH_LONG).show();
                            initializeRecyclerView();
                        } else {
                            Toast.makeText(TrackChoiceActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.rv_tracks_list);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new TrackListAdapter(TrackChoiceActivity.this, trackList, idsList);
        recyclerView.setAdapter(mAdapter);

    }
}
