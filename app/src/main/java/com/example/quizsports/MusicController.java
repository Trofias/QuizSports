package com.example.quizsports;

import android.content.Context;
import android.content.Intent;

public class MusicController {
    public static void startMusic(Context context) {
        if (!SoundPrefs.isMuted(context)) {
            Intent serviceIntent = new Intent(context, MusicService.class);
            context.startService(serviceIntent);
            setVolume(context, SoundPrefs.getMenuVolume(context, 50) / 100f);
        }
    }

    public static void pauseMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("PAUSE");
        context.startService(intent);
    }

    public static void resumeMusic(Context context) {
        if (!SoundPrefs.isMuted(context)) {
            Intent intent = new Intent(context, MusicService.class);
            intent.setAction("RESUME");
            context.startService(intent);
            setVolume(context, SoundPrefs.getMenuVolume(context, 50) / 100f);
        }
    }

    public static void stopMusic(Context context) {
        Intent serviceIntent = new Intent(context, MusicService.class);
        context.stopService(serviceIntent);
    }

    public static void setVolume(Context context, float volume) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("SET_VOLUME");
        intent.putExtra("volume", volume);
        context.startService(intent);
    }
}
