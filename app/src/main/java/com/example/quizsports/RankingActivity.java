package com.example.quizsports;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;

public class RankingActivity extends AppCompatActivity {

    private TextView rankingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        rankingTextView = findViewById(R.id.rankingTextView);

        new GetRankingTask().execute("http://172.20.10.2/obtener_puntuaciones.php");
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
                    SpannableStringBuilder rankingBuilder = new SpannableStringBuilder();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String nombre = jsonObject.getString("nombre");
                        int puntuacion = jsonObject.getInt("puntuacion");
                        String fechaHora = jsonObject.getString("fecha_hora");

                        // Asignar emoji de medalla o número de posición
                        String position;
                        switch (i) {
                            case 0:
                                position = "🥇";
                                break;
                            case 1:
                                position = "🥈";
                                break;
                            case 2:
                                position = "🥉";
                                break;
                            default:
                                position = (i + 1) + ".";
                                break;
                        }

                        // Crear el texto para cada jugador
                        String playerText = position + " " + nombre + " - " + puntuacion + " punts - " + fechaHora + "\n";
                        SpannableString spannablePlayerText = new SpannableString(playerText);

                        // Aplicar estilos según la posición
                        if (i == 0) {
                            spannablePlayerText.setSpan(new RelativeSizeSpan(1.25f), 0, playerText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannablePlayerText.setSpan(new StyleSpan(Typeface.BOLD), 0, playerText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else if (i == 1 || i == 2) {
                            spannablePlayerText.setSpan(new RelativeSizeSpan(1.15f), 0, playerText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
                            spannablePlayerText.setSpan(new RelativeSizeSpan(1.05f), 0, playerText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        // Agregar el texto al builder
                        rankingBuilder.append(spannablePlayerText);

                        // Añadir un espacio adicional entre líneas
                        if (i < jsonArray.length() - 1) {
                            rankingBuilder.append("\n");
                        }
                    }

                    rankingTextView.setText(rankingBuilder);
                } catch (Exception e) {
                    e.printStackTrace();
                    rankingTextView.setText("Error loading ranking.");
                }
            } else {
                rankingTextView.setText("Failed to connect. Please check your internet connection.");
            }
        }
    }
}

