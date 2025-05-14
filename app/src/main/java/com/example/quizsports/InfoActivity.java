package com.example.quizsports;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;


public class InfoActivity extends AppCompatActivity {

    private SoundPool soundPool;
    private int buttonClickSound;

    //gestures
    private GestureDetector gestureDetector;
    private View mainView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Configurar SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        buttonClickSound = soundPool.load(this, R.raw.button_click, 1);

        // Configurar botón de retroceso con sonido
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reproducir sonido primero
                soundPool.play(buttonClickSound, 1.0f, 1.0f, 0, 0, 1.0f);

                // Pequeño delay para que se escuche el sonido
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish(); // Cerrar la actividad
                    }
                }, 200); // 200ms de delay
            }
        });

        //gesture tornar al menu
        mainView = findViewById(android.R.id.content);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (diffX > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        // Deslizó de izquierda a derecha
                        Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Opcional
                        return true;
                    }
                }
                return false;
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundPool != null) {
            soundPool.autoPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (soundPool != null) {
            soundPool.autoResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

}