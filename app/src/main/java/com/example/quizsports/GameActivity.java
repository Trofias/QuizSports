package com.example.quizsports;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {



    // Elementos UI
    private CardView questionCard;
    private TextView questionText;
    private RadioGroup answerGroup;
    private RadioButton answer1, answer2, answer3, answer4;
    private Button btnNextQuestion;
    private Button btnBackToMenu;
    private TextView statusMessage;
    private TextView scoreCounter;

    // Lógica del juego
    private List<Question> questionList;
    private List<Question> selectedQuestions;
    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;
    private String correctAnswer;
    private String playerName;
    private boolean answerChecked = false;

    // Sistema de audio
    private MediaPlayer gameMusic;
    private SoundPool soundPool;
    private int buttonClickSound;
    private int correctAnswerSound;
    private int wrongAnswerSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // 1. Configuración del audio
        setupAudio();

        // 2. Inicialización de vistas
        initViews();

        // 3. Cargar preguntas
        loadQuestions();

        // 4. Configurar listeners con efectos de sonido
        setupListenersWithSound();
    }

    private void setupAudio() {
        // Pausar música del menú
        MusicController.pauseMusic(this);

        // Inicializar música del juego
        gameMusic = MediaPlayer.create(this, R.raw.fondo_gameactivity); // Nombre actualizado
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.7f, 0.7f);
        gameMusic.start();

        // Configurar SoundPool para efectos de sonido
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        // Cargar efectos de sonido
        buttonClickSound = soundPool.load(this, R.raw.button_click, 1);
        correctAnswerSound = soundPool.load(this, R.raw.correct_answer, 1);
        wrongAnswerSound = soundPool.load(this, R.raw.wrong_answer, 1);
    }

    private void initViews() {
        questionCard = findViewById(R.id.questionCard);
        questionText = findViewById(R.id.questionText);
        answerGroup = findViewById(R.id.answerGroup);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        statusMessage = findViewById(R.id.statusMessage);
        scoreCounter = findViewById(R.id.scoreCounter);

        playerName = getIntent().getStringExtra("playerName");
    }

    private void loadQuestions() {
        new GetQuestionsTask().execute("http://172.20.10.2/obtener_preguntas.php");
    }

    private void setupListenersWithSound() {
        // Listener para el grupo de respuestas
        answerGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!answerChecked) {
                playSound(buttonClickSound);
                RadioButton selected = findViewById(checkedId);
                if (selected != null) {
                    checkAnswer(selected);
                    disableOtherAnswers(selected.getId());
                    answerChecked = true;
                }
            }
        });

        // Listener para el botón siguiente
        btnNextQuestion.setOnClickListener(v -> {
            playSound(buttonClickSound);
            if (answerChecked) {
                loadNextQuestion();
            } else {
                Toast.makeText(this, "Por favor selecciona una respuesta", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para el botón volver al menú
        btnBackToMenu.setOnClickListener(v -> {
            playSound(buttonClickSound);
            goBackToMenu();
        });
    }

    private void playSound(int soundId) {
        soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameMusic != null && gameMusic.isPlaying()) {
            gameMusic.pause();
        }
        soundPool.autoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameMusic != null && !gameMusic.isPlaying()) {
            gameMusic.start();
        }
        soundPool.autoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseAudioResources();
    }

    private void releaseAudioResources() {
        // Liberar música de fondo
        if (gameMusic != null) {
            gameMusic.release();
            gameMusic = null;
        }

        // Liberar SoundPool
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        // Reanudar música del menú
        MusicController.resumeMusic(this);
    }

    private void checkAnswer(RadioButton selected) {
        if (selected.getText().toString().equals(correctAnswer)) {
            playSound(correctAnswerSound);
            selected.setBackgroundResource(R.color.correctAnswer);
            Toast.makeText(this, "Correcto!", Toast.LENGTH_SHORT).show();
            correctAnswersCount++;
        } else {
            playSound(wrongAnswerSound);
            selected.setBackgroundResource(R.color.incorrectAnswer);
            Toast.makeText(this, "Incorrecto! La respuesta era: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }
        updateScoreCounter();
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < selectedQuestions.size()) {
            Question question = selectedQuestions.get(currentQuestionIndex);
            questionText.setText(question.getQuestionText());

            List<Answer> answers = question.getAnswers();
            Collections.shuffle(answers);

            for (int i = 0; i < answers.size(); i++) {
                Answer answer = answers.get(i);
                switch (i) {
                    case 0:
                        answer1.setText(answer.getAnswerText());
                        if (answer.isCorrect()) correctAnswer = answer.getAnswerText();
                        break;
                    case 1:
                        answer2.setText(answer.getAnswerText());
                        if (answer.isCorrect()) correctAnswer = answer.getAnswerText();
                        break;
                    case 2:
                        answer3.setText(answer.getAnswerText());
                        if (answer.isCorrect()) correctAnswer = answer.getAnswerText();
                        break;
                    case 3:
                        answer4.setText(answer.getAnswerText());
                        if (answer.isCorrect()) correctAnswer = answer.getAnswerText();
                        break;
                }
            }

            resetRadioButtonBackgrounds();
            answerGroup.clearCheck();
            enableAllAnswers();
            answerChecked = false;
            updateScoreCounter();
            currentQuestionIndex++;
        } else {
            endGame();
        }
    }

    private void endGame() {
        String resultMessage = "Fin del juego, " + playerName + "! Has acertado " + correctAnswersCount + " de 10.";
        statusMessage.setText(resultMessage);
        new SaveScoreTask().execute("http://172.20.10.2/guardar_puntuacion.php", playerName, String.valueOf(correctAnswersCount));
        btnBackToMenu.setVisibility(View.VISIBLE);
    }

    private void disableOtherAnswers(int selectedId) {
        for (RadioButton rb : new RadioButton[]{answer1, answer2, answer3, answer4}) {
            if (rb.getId() != selectedId) {
                rb.setEnabled(false);
            }
        }
    }

    private void enableAllAnswers() {
        for (RadioButton rb : new RadioButton[]{answer1, answer2, answer3, answer4}) {
            rb.setEnabled(true);
        }
    }

    private void resetRadioButtonBackgrounds() {
        for (RadioButton rb : new RadioButton[]{answer1, answer2, answer3, answer4}) {
            rb.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

    private void updateScoreCounter() {
        scoreCounter.setText(correctAnswersCount + "/" + currentQuestionIndex);
    }

    private void goBackToMenu() {
        Intent menuIntent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(menuIntent);
        finish();
    }

    private class GetQuestionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            statusMessage.setText("Cargando preguntas...");
        }

        @Override
        protected String doInBackground(String... params) {
            HttpHandler httpHandler = new HttpHandler();
            return httpHandler.makeServiceCall(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                statusMessage.setText("Error de conexión. Revisa tu internet.");
                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(result);
                questionList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int questionId = jsonObject.getInt("pregunta_id");
                    String questionText = jsonObject.getString("pregunta");
                    List<Answer> answers = new ArrayList<>();

                    JSONArray answersArray = jsonObject.getJSONArray("respuestas");
                    for (int j = 0; j < answersArray.length(); j++) {
                        JSONObject answerObject = answersArray.getJSONObject(j);
                        int answerId = answerObject.getInt("respuesta_id");
                        String answerText = answerObject.getString("respuesta");
                        boolean isCorrect = answerObject.getInt("es_correcta") == 1;
                        answers.add(new Answer(answerId, answerText, isCorrect));
                    }

                    questionList.add(new Question(questionId, questionText, answers));
                }

                Collections.shuffle(questionList);
                selectedQuestions = new ArrayList<>(questionList.subList(0, Math.min(10, questionList.size())));

                statusMessage.setText("");
                loadNextQuestion();
            } catch (Exception e) {
                e.printStackTrace();
                statusMessage.setText("Error cargando preguntas. Intenta más tarde.");
            }
        }
    }

    private class SaveScoreTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String playerName = params[1];
            String score = params[2];

            HttpHandler httpHandler = new HttpHandler();
            String urlWithParams = url + "?nombre=" + playerName + "&puntuacion=" + score;
            return httpHandler.makeServiceCall(urlWithParams);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(GameActivity.this, "Puntuación guardada!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GameActivity.this, "Error guardando puntuación", Toast.LENGTH_SHORT).show();
            }
        }


    }
}