package com.example.quizsports;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class RankingActivity extends AppCompatActivity {

    private TextView rankingTextView;
    private SoundPool soundPool;
    private int buttonClickSound;

    //gestures
    private GestureDetector gestureDetector;
    private View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // 1. Configurar SoundPool para efectos de sonido
        setupSoundEffects();

        // 2. Inicializar vistas
        rankingTextView = findViewById(R.id.rankingTextView);
        Button btnBack = findViewById(R.id.btnBack);

        // 3. Configurar botón de volver con sonido
        setupBackButton(btnBack);

        // 4. Cargar datos del ranking
        loadRankingData();

        //gesture ir al menu
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
                        // Swipe de izquierda a derecha
                        Intent intent = new Intent(RankingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // opcional si no quieres que quede en el stack
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void setupSoundEffects() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        buttonClickSound = soundPool.load(this, R.raw.button_click, 1);
    }

    private void setupBackButton(Button btnBack) {
        btnBack.setOnClickListener(v -> {
            // Reproducir sonido y cerrar actividad
            playSound(buttonClickSound);
            v.postDelayed(this::finish, 200); // Delay para escuchar el sonido
        });
    }

    private void loadRankingData() {
        new GetRankingTask().execute("http://172.20.10.2/obtener_puntuaciones.php");
    }

    private void playSound(int soundId) {
        soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
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

    private class GetRankingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpHandler httpHandler = new HttpHandler();
            return httpHandler.makeServiceCall(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    StringBuilder rankingBuilder = new StringBuilder();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String nombre = jsonObject.getString("nombre");
                        int puntuacion = jsonObject.getInt("puntuacion");
                        String fechaHora = jsonObject.getString("fecha_hora");

                        String position;
                        switch (i) {
                            case 0: position = "🥇"; break;
                            case 1: position = "🥈"; break;
                            case 2: position = "🥉"; break;
                            default: position = (i + 1) + "."; break;
                        }

                        rankingBuilder.append(position)
                                .append(" ")
                                .append(nombre)
                                .append(" - ")
                                .append(puntuacion)
                                .append(" pts - ")
                                .append(fechaHora)
                                .append("\n\n");
                    }

                    rankingTextView.setText(rankingBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    rankingTextView.setText("Error al cargar el ranking");
                }
            } else {
                rankingTextView.setText("Error de conexión");
            }
        }


    }
}