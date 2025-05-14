package com.example.quizsports;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Elementos de UI
    private Button btnStartGame;
    private Button btnInfo;
    private Button btnRanking;
    private ImageButton btnSettings;

    // Sistema de audio
    private SoundPool soundPool;
    private int buttonClickSound;

    //gestures
    private GestureDetector gestureDetector;
    private View mainView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Configurar sistema de sonido
        setupSoundSystem();

        // 2. Inicializar vistas
        initViews();

        // 3. Configurar listeners
        setupListeners();

        // 4. Iniciar música de fondo
        MusicController.startMusic(this);


        //sortir de la app
        mainView = findViewById(android.R.id.content);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();

                if (Math.abs(diffY) < 0) return false; // Protección extra

                if (Math.abs(diffY) > Math.abs(diffX)) {
                    if (diffY < 0 && Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        // Swipe hacia arriba detectado
                        finishAffinity(); // Cierra la aplicación completamente
                        return true;
                    }
                }
                return false;
            }
        });



    }

    private void setupSoundSystem() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        buttonClickSound = soundPool.load(this, R.raw.button_click, 1);
    }

    private void initViews() {
        btnStartGame = findViewById(R.id.btnStartGame);
        btnInfo = findViewById(R.id.btnInfo);
        btnRanking = findViewById(R.id.btnRanking);
        btnSettings = findViewById(R.id.btnSettings);
    }

    private void setupListeners() {
        // Botón Nueva Partida
        btnStartGame.setOnClickListener(v -> {
            playSound(buttonClickSound);
            showNameDialog();
        });

        // Botón Información
        btnInfo.setOnClickListener(v -> {
            playSound(buttonClickSound);
            navigateWithDelay(InfoActivity.class);
        });

        // Botón Clasificación
        btnRanking.setOnClickListener(v -> {
            playSound(buttonClickSound);
            navigateWithDelay(RankingActivity.class);
        });

        // Botón Ajustes
        btnSettings.setOnClickListener(v -> {
            playSound(buttonClickSound);
            navigateWithDelay(SoundSettingsActivity.class);
        });
    }

    private void playSound(int soundId) {
        if (!SoundPrefs.isMuted(this)) {
            float volume = SoundPrefs.getEffectsVolume(this, 50) / 100f;
            soundPool.play(soundId, volume, volume, 0, 0, 1.0f);
        }
    }

    private void navigateWithDelay(Class<?> activityClass) {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, activityClass));
        }, 200);
    }

    private void showNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escribe tu nombre");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            playSound(buttonClickSound);
            String playerName = input.getText().toString().trim();
            if (!playerName.isEmpty()) {
                startGame(playerName);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            playSound(buttonClickSound);
            dialog.cancel();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        playSound(buttonClickSound);
    }

    private void startGame(String playerName) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("playerName", playerName);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundPool.autoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundPool.autoResume();
        if (!SoundPrefs.isMuted(this)) {
            MusicController.resumeMusic(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
        if (isFinishing()) {
            MusicController.stopMusic(this);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

}