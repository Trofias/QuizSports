package com.example.quizsports;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;

public class GameActivity extends AppCompatActivity {

    private CardView questionCard;
    private TextView questionText;
    private RadioGroup answerGroup;
    private RadioButton answer1, answer2, answer3, answer4;
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

        loadNextQuestion();

        answerGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            checkAnswer(selected);
        });
    }

    private void loadNextQuestion() {
        // Simulated question (replace with database retrieval)
        questionText.setText("What is the capital of France?");
        answer1.setText("Paris");
        answer2.setText("London");
        answer3.setText("Berlin");
        answer4.setText("Madrid");
        correctAnswer = "Paris";

        // Reset selection
        answerGroup.clearCheck();
    }

    private void checkAnswer(RadioButton selected) {
        if (selected.getText().toString().equals(correctAnswer)) {
            selected.setBackgroundResource(R.color.correctAnswer);
        } else {
            selected.setBackgroundResource(R.color.incorrectAnswer);
        }
        flipCardAnimation();
    }

    private void flipCardAnimation() {
        ObjectAnimator flipOut = ObjectAnimator.ofFloat(questionCard, "rotationY", 0f, 90f);
        ObjectAnimator flipIn = ObjectAnimator.ofFloat(questionCard, "rotationY", -90f, 0f);
        flipOut.setDuration(300);
        flipIn.setDuration(300);
        AnimatorSet flipAnimation = new AnimatorSet();
        flipAnimation.playSequentially(flipOut, flipIn);
        flipAnimation.start();
    }
}
