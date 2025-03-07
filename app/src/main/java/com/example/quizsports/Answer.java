package com.example.quizsports;
public class Answer {
    private int id;
    private String answerText;
    private boolean isCorrect;

    public Answer(int id, String answerText, boolean isCorrect) {
        this.id = id;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }

    public int getId() {
        return id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
