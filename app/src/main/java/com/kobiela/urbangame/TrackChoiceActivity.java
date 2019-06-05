package com.kobiela.urbangame;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
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
    private final List<String> idsListFull = new ArrayList<>();
    private final List<Track> trackListFull = new ArrayList<>();
    private List<String> idsList = new ArrayList<>();
    private List<Track> trackList = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private EditText etSearchTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_choice);

        etSearchTrack = findViewById(R.id.et_search_track);
        etSearchTrack.setEnabled(false);
        etSearchTrack.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*readData(s.toString());
                Toast.makeText(TrackChoiceActivity.this, s.toString(),Toast.LENGTH_SHORT).show();*/
                trackList.clear();
                idsList.clear();
                for (int i = 0; i < trackListFull.size(); i++) {

                    if(trackListFull.get(i).getTitle().toLowerCase().contains(s.toString().toLowerCase())
                    || trackListFull.get(i).getDescription().toLowerCase().contains(s.toString().toLowerCase())
                    || trackListFull.get(i).getLocation().toLowerCase().contains(s.toString().toLowerCase())){
                        trackList.add(trackListFull.get(i));
                        idsList.add(idsListFull.get(i));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });

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
                                String title = document.get("TITLE").toString();
                                String description = document.get("DESCRIPTION").toString();
                                String location = document.get("LOCATION").toString();
                                String author = document.get("AUTHOR").toString();
                                int qualitySum = Integer.valueOf(document.get("QUALITY_SUM").toString());
                                int difficultySum = Integer.valueOf(document.get("DIFFICULTY_SUM").toString());
                                int lengthSum = Integer.valueOf(document.get("LENGTH_SUM").toString());
                                int numberOfVotes = Integer.valueOf(document.get("NUMBER_OF_VOTES").toString());
                                trackListFull.add(new Track(title,description,location,author,qualitySum,difficultySum,lengthSum,numberOfVotes));
                                trackList.add(new Track(title,description,location,author,qualitySum,difficultySum,lengthSum,numberOfVotes));
                                idsListFull.add(document.getId());
                                idsList.add(document.getId());
                            }
                            initializeRecyclerView();
                        } else {
                            Toast.makeText(TrackChoiceActivity.this, getString(R.string.toast_something_wrong), Toast.LENGTH_LONG).show();
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
        etSearchTrack.setEnabled(true);
    }
}
