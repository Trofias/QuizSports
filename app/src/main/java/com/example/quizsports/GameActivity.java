package com.example.quizsports;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private CardView questionCard;
    private TextView questionText;
    private RadioGroup answerGroup;
    private RadioButton answer1, answer2, answer3, answer4;
    private Button btnNextQuestion;
    private Button btnBackToMenu;
    private TextView statusMessage;
    private TextView scoreCounter;
    private List<Question> questionList;
    private List<Question> selectedQuestions;
    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;
    private String correctAnswer;
    private String playerName;
    private boolean answerChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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

        new GetQuestionsTask().execute("http://172.20.10.2/obtener_preguntas.php");

        answerGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!answerChecked) {
                RadioButton selected = findViewById(checkedId);
                if (selected != null) {
                    checkAnswer(selected);
                    disableOtherAnswers(selected.getId());
                    answerChecked = true;
                }
            }
        });

        btnNextQuestion.setOnClickListener(v -> {
            if (answerChecked) {
                loadNextQuestion();
            } else {
                Toast.makeText(this, "Please select an answer.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBackToMenu.setOnClickListener(v -> goBackToMenu());
    }

    private class GetQuestionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            statusMessage.setText("Loading questions...");
        }

        @Override
        protected String doInBackground(String... params) {
            HttpHandler httpHandler = new HttpHandler();
            return httpHandler.makeServiceCall(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                statusMessage.setText("Failed to connect. Please check your internet connection.");
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

                // Seleccionar 10 preguntas aleatorias
                Collections.shuffle(questionList);
                selectedQuestions = new ArrayList<>(questionList.subList(0, Math.min(10, questionList.size())));

                statusMessage.setText(""); // Clear the status message
                loadNextQuestion();
            } catch (Exception e) {
                e.printStackTrace();
                statusMessage.setText("Error loading questions. Please try again later.");
            }
        }
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < selectedQuestions.size()) {
            Question question = selectedQuestions.get(currentQuestionIndex);
            questionText.setText(question.getQuestionText());

            List<Answer> answers = question.getAnswers();
            // Desordenar las respuestas
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

            // Restablecer el fondo de los RadioButton
            resetRadioButtonBackgrounds();

            answerGroup.clearCheck();
            enableAllAnswers();
            answerChecked = false;
            updateScoreCounter();
            currentQuestionIndex++;
        } else {
            // Mostrar resultado y guardar puntuación
            String resultMessage = "Game Over, " + playerName + "! You got " + correctAnswersCount + " out of 10 correct.";
            statusMessage.setText(resultMessage);
            new SaveScoreTask().execute("http://172.20.10.2/guardar_puntuacion.php", playerName, String.valueOf(correctAnswersCount));

            // Hacer visible el botón de volver al menú
            btnBackToMenu.setVisibility(View.VISIBLE);
        }
    }

    private void checkAnswer(RadioButton selected) {
        if (selected.getText().toString().equals(correctAnswer)) {
            selected.setBackgroundResource(R.color.correctAnswer);
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            correctAnswersCount++;
        } else {
            selected.setBackgroundResource(R.color.incorrectAnswer);
            Toast.makeText(this, "Wrong! The correct answer was: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }
        updateScoreCounter();
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
            rb.setBackgroundResource(android.R.drawable.btn_default); // Restablecer al fondo predeterminado
        }
    }

    private void updateScoreCounter() {
        scoreCounter.setText(correctAnswersCount + "/" + currentQuestionIndex);
    }

    private void goBackToMenu() {
        Intent menuIntent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(menuIntent);
        finish(); // Cierra la actividad actual para que no se pueda volver atrás
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
                Toast.makeText(GameActivity.this, "Score saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GameActivity.this, "Failed to save score.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

