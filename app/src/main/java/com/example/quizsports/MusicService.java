package com.example.quizsports;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private MediaPlayer player;
    private static boolean isRunning = false;
    private float currentVolume = 0.5f;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Servicio de música creado");
        player = MediaPlayer.create(this, R.raw.musica_menu);
        player.setLooping(true);
        player.setVolume(currentVolume, currentVolume);
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "PAUSE":
                    if (player != null && player.isPlaying()) {
                        player.pause();
                    }
                    break;
                case "RESUME":
                    if (player != null && !player.isPlaying()) {
                        player.start();
                        player.setVolume(currentVolume, currentVolume);
                    }
                    break;
                case "SET_VOLUME":
                    float volume = intent.getFloatExtra("volume", 0.5f);
                    currentVolume = volume;
                    if (player != null) {
                        player.setVolume(volume, volume);
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
        isRunning = false;
        Log.d(TAG, "Servicio de música destruido");
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
