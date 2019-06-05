package com.kobiela.urbangame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {
    public static double round(double value, int roundingAcc) {
        if (roundingAcc < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(roundingAcc, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void removeDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void removeAllDefaults(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static boolean searchDefaults(String key, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.contains(key);
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static void changeMapType(Context context, int numberMapType, GoogleMap mMap) {
        switch (numberMapType){
            case 0: {
                mMap.setMapStyle(new MapStyleOptions("[]"));
                break;
            }
            case 1: {
                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                context, R.raw.retro_theme));
                break;
            }
            case 2: mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); break;
            case 3: mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); break;
            case 4: mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN); break;
            case 5: {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                context, R.raw.night_theme));
                break;
            }
        }
    }

    public static void openRatingDialogWindow(final String id, String title, final Context context, final boolean closeContextActivity) {
        final Dialog rateDialog = new Dialog(context);
        rateDialog.setContentView(R.layout.popup_track_rate);

        TextView tvRateTrackTitle = rateDialog.findViewById(R.id.tv_rate_track_title);
        Button bCancelRating = rateDialog.findViewById(R.id.b_cancel_rating);
        Button bSaveRating = rateDialog.findViewById(R.id.b_save_rating);
        final RatingBar ratbQuality = rateDialog.findViewById(R.id.ratb_quality);
        final RatingBar ratbDifficulty = rateDialog.findViewById(R.id.ratb_difficulty);
        final RatingBar ratbLength = rateDialog.findViewById(R.id.ratb_length);

        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        rateDialog.show();

        int savedQuality = 0;
        int savedDifficulty = 0;
        int savedLength = 0;
        boolean alreadyRated = false;

        if(Utils.searchDefaults(id + "_QUALITY", context)){
            savedQuality = Integer.valueOf(Utils.getDefaults(id + "_QUALITY", context));
            ratbQuality.setRating(Float.valueOf(Utils.getDefaults(id + "_QUALITY", context)));
            alreadyRated = true;
        }
        if(Utils.searchDefaults(id + "_DIFFICULTY", context)){
            savedDifficulty = Integer.valueOf(Utils.getDefaults(id + "_DIFFICULTY", context));
            ratbDifficulty.setRating(Float.valueOf(Utils.getDefaults(id + "_DIFFICULTY", context)));
            alreadyRated = true;
        }
        if(Utils.searchDefaults(id + "_LENGTH", context)){
            savedLength = Integer.valueOf(Utils.getDefaults(id + "_LENGTH", context));
            ratbLength.setRating(Float.valueOf(Utils.getDefaults(id + "_LENGTH", context)));
            alreadyRated = true;
        }

        final int savedQualityFinal = savedQuality;
        final int savedDifficultyFinal = savedDifficulty;
        final int savedLengthFinal = savedLength;
        final boolean alreadyRatedFinal = alreadyRated;

        tvRateTrackTitle.setText(title);
        bCancelRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
            }
        });

        bSaveRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qualityRate = Math.round(ratbQuality.getRating());
                int difficultyRate = Math.round(ratbDifficulty.getRating());
                int lengthRate = Math.round(ratbLength.getRating());

                final int qualityDifference = qualityRate - savedQualityFinal;
                final int difficultyDifference = difficultyRate - savedDifficultyFinal;
                final int lengthDifference = lengthRate - savedLengthFinal;

                Utils.setDefaults(id + "_QUALITY", String.valueOf(qualityRate), context);
                Utils.setDefaults(id + "_DIFFICULTY", String.valueOf(difficultyRate), context);
                Utils.setDefaults(id + "_LENGTH", String.valueOf(lengthRate), context);

                final DocumentReference docRef = FirebaseFirestore.getInstance().collection("tracksCollection").document(id);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int firestoreSavedQualitySum = Integer.valueOf(documentSnapshot.get("QUALITY_SUM").toString());
                        int firestoreSavedDifficultySum = Integer.valueOf(documentSnapshot.get("DIFFICULTY_SUM").toString());
                        int firestoreSavedLengthSum = Integer.valueOf(documentSnapshot.get("LENGTH_SUM").toString());
                        int firestoreSavedVotesNumber = Integer.valueOf(documentSnapshot.get("NUMBER_OF_VOTES").toString());

                        int newQualityRateToSave = firestoreSavedQualitySum + qualityDifference;
                        int newDifficultyRateToSave = firestoreSavedDifficultySum + difficultyDifference;
                        int newLengthRateToSave = firestoreSavedLengthSum + lengthDifference;
                        int newVotesNumber = alreadyRatedFinal?firestoreSavedVotesNumber:(firestoreSavedVotesNumber+1);


                        docRef.update("QUALITY_SUM", newQualityRateToSave,
                                        "DIFFICULTY_SUM", newDifficultyRateToSave,
                                        "LENGTH_SUM", newLengthRateToSave,
                                        "NUMBER_OF_VOTES", newVotesNumber);


                    }
                });

                rateDialog.dismiss();
            }
        });

        rateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(closeContextActivity) {
                    ((Activity) context).finish();
                }
            }
        });


    }

}
