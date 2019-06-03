package com.kobiela.urbangame;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private VideoView vvBackground;
    private MediaPlayer mediaPlayer;
    private Button bPlay;
    private Button bCreate;
    private Button bInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        vvBackground = findViewById(R.id.videoView);

        handleButtons();
        handleVideoBackground();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0) {
                    if(grantResults[0] == -1) {
                        this.finish();
                    }
                }
                break;
        }
    }

    private void handleVideoBackground() {
        final Uri uri1 = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.background_merged);

        vvBackground.setVideoURI(uri1);
        vvBackground.start();

        vvBackground.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                MainActivity.this.mediaPlayer = mediaPlayer;
                mediaPlayer.setLooping(true);
            }
        });
    }

    public void handleButtons(){
        bPlay = findViewById(R.id.b_play);
        bCreate = findViewById(R.id.b_create);
        bInfo = findViewById(R.id.b_info);

        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TrackChoiceActivity.class));
            }
        });

        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        bInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.popup_info_maps);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView iv_close_info = dialog.findViewById(R.id.ic_ma_close_info);
                TextView tv_game_info_first = dialog.findViewById(R.id.tv_info_map_first);
                TextView tv_game_info_second = dialog.findViewById(R.id.tv_info_map_second);
                TextView tv_game_info_third = dialog.findViewById(R.id.tv_info_map_third);

                tv_game_info_first.setText(R.string.tv_first);
                tv_game_info_second.setText(R.string.tv_second);
                tv_game_info_third.setText(R.string.tv_third);

                tv_game_info_first.setTextSize(12);
                tv_game_info_second.setTextSize(12);
                tv_game_info_third.setTextSize(12);

                iv_close_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart the video when resuming the Activity
        vvBackground.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // When the Activity is destroyed, release our MediaPlayer and set it to null.
        mediaPlayer.release();
        mediaPlayer = null;
    }



}
