package com.example.quizsports;

import android.content.Context;
import android.content.SharedPreferences;

public class SoundPrefs {
    private static final String PREFS_NAME = "SoundSettings";
    public static final String KEY_MENU_VOLUME = "menu_volume";
    public static final String KEY_GAME_VOLUME = "game_volume";
    public static final String KEY_EFFECTS_VOLUME = "effects_volume";
    public static final String KEY_MUTED = "muted";

    public static int getMenuVolume(Context context, int defaultValue) {
        return getSharedPreferences(context).getInt(KEY_MENU_VOLUME, defaultValue);
    }

    public static int getGameVolume(Context context, int defaultValue) {
        return getSharedPreferences(context).getInt(KEY_GAME_VOLUME, defaultValue);
    }

    public static int getEffectsVolume(Context context, int defaultValue) {
        return getSharedPreferences(context).getInt(KEY_EFFECTS_VOLUME, defaultValue);
    }

    public static boolean isMuted(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_MUTED, false);
    }

    public static void setMenuVolume(Context context, int volume) {
        getSharedPreferences(context).edit().putInt(KEY_MENU_VOLUME, volume).apply();
    }

    public static void setGameVolume(Context context, int volume) {
        getSharedPreferences(context).edit().putInt(KEY_GAME_VOLUME, volume).apply();
    }

    public static void setEffectsVolume(Context context, int volume) {
        getSharedPreferences(context).edit().putInt(KEY_EFFECTS_VOLUME, volume).apply();
    }

    public static void setMuted(Context context, boolean muted) {
        getSharedPreferences(context).edit().putBoolean(KEY_MUTED, muted).apply();
    }

    public static void setVolume(Context context, String key, int volume) {
        getSharedPreferences(context).edit().putInt(key, volume).apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
