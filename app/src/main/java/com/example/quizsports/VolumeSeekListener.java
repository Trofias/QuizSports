package com.example.quizsports;

import android.content.Context;
import android.widget.SeekBar;

public class VolumeSeekListener implements SeekBar.OnSeekBarChangeListener {
    private final Context context;
    private final String volumeKey;

    public VolumeSeekListener(Context context, String volumeKey) {
        this.context = context;
        this.volumeKey = volumeKey;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        SoundPrefs.setVolume(context, volumeKey, progress);

        if (volumeKey.equals(SoundPrefs.KEY_MENU_VOLUME)) {
            MusicController.setVolume(context, progress / 100f);
        }
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override public void onStopTrackingTouch(SeekBar seekBar) {}
}