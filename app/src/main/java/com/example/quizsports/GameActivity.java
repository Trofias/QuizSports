package com.example.quizsports;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
    private TextView statusMessage;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private String correctAnswer;

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
        statusMessage = findViewById(R.id.statusMessage);

        new GetQuestionsTask().execute("http://172.20.10.2/obtener_preguntas.php");

        answerGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            if (selected != null) {
                checkAnswer(selected);
            }
        });

        btnNextQuestion.setOnClickListener(v -> loadNextQuestion());
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

                // Desordenar las preguntas
                Collections.shuffle(questionList);

                statusMessage.setText(""); // Clear the status message
                loadNextQuestion();
            } catch (Exception e) {
                e.printStackTrace();
                statusMessage.setText("Error loading questions. Please try again later.");
            }
        }
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question question = questionList.get(currentQuestionIndex);
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

            answerGroup.clearCheck();
            currentQuestionIndex++;
        } else {
            // Manejar el final de las preguntas
            statusMessage.setText("No more questions available.");
        }
    }

    private void checkAnswer(RadioButton selected) {
        if (selected.getText() != null && selected.getText().toString().equals(correctAnswer)) {
            selected.setBackgroundResource(R.color.correctAnswer);
        } else {
            selected.setBackgroundResource(R.color.incorrectAnswer);
        }
        flipCardAnimation();
    }

    private void flipCardAnimation() {
        // Implementación de la animación
    }
}
