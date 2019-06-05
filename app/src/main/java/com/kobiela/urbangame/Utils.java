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

        if(Utils.searchDefaults(id + "_QUALITY", context)){
            ratbQuality.setRating(Float.valueOf(Utils.getDefaults(id + "_QUALITY", context)));
        }
        if(Utils.searchDefaults(id + "_DIFFICULTY", context)){
            ratbDifficulty.setRating(Float.valueOf(Utils.getDefaults(id + "_DIFFICULTY", context)));
        }
        if(Utils.searchDefaults(id + "_LENGTH", context)){
            ratbLength.setRating(Float.valueOf(Utils.getDefaults(id + "_LENGTH", context)));
        }


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
                Utils.setDefaults(id + "_QUALITY", String.valueOf(ratbQuality.getRating()), context);
                Utils.setDefaults(id + "_DIFFICULTY", String.valueOf(ratbDifficulty.getRating()), context);
                Utils.setDefaults(id + "_LENGTH", String.valueOf(ratbLength.getRating()), context);
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
