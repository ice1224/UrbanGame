package com.kobiela.urbangame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder> {
    private List<Track> trackList;
    private List<String> idsList;
    private Context context;

    public TrackListAdapter(Context context, List<Track> trackList, List<String> idsList) {
        this.context = context;
        this.trackList = trackList;
        this.idsList = idsList;
    }

    @NonNull
    @Override
    public TrackListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_track_item, parent, false);
        return new TrackListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListViewHolder holder, final int position) {
        final String title = trackList.get(position).getTitle();
        final String description = trackList.get(position).getDescription();
        final String location = trackList.get(position).getLocation();
        final String author = trackList.get(position).getAuthor();
        final float qualityAverage = trackList.get(position).getQualityAverage();

        holder.trackLocation.setText(location);
        holder.trackTitle.setText(title);
        holder.qualityAverage.setText(String.valueOf(qualityAverage));

        if (Utils.searchDefaults(idsList.get(position), context)) {
            holder.trackTitle.setTextColor(ContextCompat.getColor(context, R.color.colorAccentOther));
        } else {
            holder.trackTitle.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        if (Utils.searchDefaults(idsList.get(position) + "_FINISHED", context)) {
            holder.parentLayout.setBackgroundResource(R.color.colorAccentOther);
            holder.ratingStarIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
            if (Utils.searchDefaults(idsList.get(position), context)) {
                holder.trackTitle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        } else {
            holder.parentLayout.setBackgroundResource(R.color.colorPrimary);
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogWindow(position, title, description, location, author);
            }
        });
    }

    private void openDialogWindow(final int position, final String title, String description, String location, String author) {
        final Dialog dialog = new Dialog(context);

        if (Utils.searchDefaults(idsList.get(position) + "_FINISHED", context)) {
            dialog.setContentView(R.layout.popup_track_details_rate);
            Button bRateTrack = dialog.findViewById(R.id.b_rate_track);
            bRateTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Utils.openRatingDialogWindow(idsList.get(position), trackList.get(position), context, false);
                }


            });
        } else {
            dialog.setContentView(R.layout.popup_track_details);
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView tvTrackTitle = dialog.findViewById(R.id.tv_track_details_title);
        TextView tvTrackDescription = dialog.findViewById(R.id.tv_track_details_description);
        TextView tvTrackLocation = dialog.findViewById(R.id.tv_track_details_location);
        TextView tvTrackAuthor = dialog.findViewById(R.id.tv_track_details_author);
        TextView tvQualityRateNumber = dialog.findViewById(R.id.tv_rating_star_icon_quality_number);
        TextView tvDifficultyRateNumber = dialog.findViewById(R.id.tv_rating_star_icon_difficulty_number);
        TextView tvLengthRateNumber = dialog.findViewById(R.id.tv_rating_star_icon_length_number);
        Button bPlayGame = dialog.findViewById(R.id.b_play_game);
        Button bCancelGame = dialog.findViewById(R.id.b_cancel_game);


        tvTrackTitle.setText(title);
        tvTrackDescription.setText(description);

        tvQualityRateNumber.setText(String.valueOf(trackList.get(position).getQualityAverage()));
        tvDifficultyRateNumber.setText(String.valueOf(trackList.get(position).getDifficultyAverage()));
        tvLengthRateNumber.setText(String.valueOf(trackList.get(position).getLengthAverage()));

        tvTrackLocation.setText(location);
        tvTrackAuthor.setText(author);

        bPlayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(idsList.get(position), position);
                dialog.cancel();
            }
        });

        bCancelGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void startGame(final String id, final int position) {
        FirebaseFirestore.getInstance().collection("tracksCollection").document(id).collection("riddles")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Riddle> riddles = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String lat = ((HashMap) document.get("COORDS")).get("latitude").toString();
                                String lng = ((HashMap) document.get("COORDS")).get("longitude").toString();
                                String riddle = document.get("RIDDLE").toString();

                                riddles.add(new Riddle(lat, lng, riddle));
                            }
                            Intent intent = new Intent(context, GameActivity.class);
                            trackList.get(position).setRiddles(riddles);
                            // intent.putStringArrayListExtra("GAME", game);
                            intent.putExtra("TRACK_ID", id);
                            intent.putExtra("TRACK", trackList.get(position));
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        } else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public static class TrackListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView trackTitle, trackLocation, qualityAverage;
        ImageView ratingStarIcon;
        ConstraintLayout parentLayout;

        public TrackListViewHolder(View itemView) {
            super(itemView);
            trackTitle = itemView.findViewById(R.id.tv_item_track_title);
            trackLocation = itemView.findViewById(R.id.tv_item_track_location);
            qualityAverage = itemView.findViewById(R.id.tv_quality_rating);
            ratingStarIcon = itemView.findViewById(R.id.iv_rating_star_icon);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }


}
