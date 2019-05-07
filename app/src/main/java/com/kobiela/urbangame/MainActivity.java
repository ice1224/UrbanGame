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
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private VideoView vvBackground;
    MediaPlayer mediaPlayer;
    int mCurrentVideoPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        vvBackground = findViewById(R.id.videoView);

        handleVideoBackground();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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

                if (mCurrentVideoPosition != 0) {
                    mediaPlayer.seekTo(mCurrentVideoPosition);
                    mediaPlayer.start();
                }
            }
        });
    }

    public void onClick(View view) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void onClick2(View view) {
        Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.popup_success);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }


/*


    @Override
    protected void onPause() {
        super.onPause();
        // Capture the current video position and pause the video.
        mCurrentVideoPosition = mediaPlayer.getCurrentPosition();
        vvBackground.pause();
    }
*/
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
