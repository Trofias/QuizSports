package com.example.quizsports;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class SoundSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_settings);

        SeekBar menuMusicSeekBar = findViewById(R.id.menuMusicSeekBar);
        SeekBar gameMusicSeekBar = findViewById(R.id.gameMusicSeekBar);
        SeekBar soundEffectsSeekBar = findViewById(R.id.soundEffectsSeekBar);
        Switch muteSwitch = findViewById(R.id.muteSwitch);

        // Cargar valores actuales
        menuMusicSeekBar.setProgress(SoundPrefs.getMenuVolume(this, 50));
        gameMusicSeekBar.setProgress(SoundPrefs.getGameVolume(this, 50));
        soundEffectsSeekBar.setProgress(SoundPrefs.getEffectsVolume(this, 50));
        muteSwitch.setChecked(SoundPrefs.isMuted(this));

        // Configurar listeners
        menuMusicSeekBar.setOnSeekBarChangeListener(new VolumeSeekListener(this, SoundPrefs.KEY_MENU_VOLUME));
        gameMusicSeekBar.setOnSeekBarChangeListener(new VolumeSeekListener(this, SoundPrefs.KEY_GAME_VOLUME));
        soundEffectsSeekBar.setOnSeekBarChangeListener(new VolumeSeekListener(this, SoundPrefs.KEY_EFFECTS_VOLUME));

        muteSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SoundPrefs.setMuted(this, isChecked);
            if (isChecked) {
                MusicController.pauseMusic(this);
            } else {
                MusicController.resumeMusic(this);
            }

        });
    }
}