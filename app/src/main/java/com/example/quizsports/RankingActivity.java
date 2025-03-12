package com.example.quizsports;

import android.os.AsyncTask;
import android.os.Bundle;
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
                    StringBuilder rankingBuilder = new StringBuilder();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String nombre = jsonObject.getString("nombre");
                        int puntuacion = jsonObject.getInt("puntuacion");
                        String fechaHora = jsonObject.getString("fecha_hora");

                        rankingBuilder.append(nombre)
                                .append(" - ")
                                .append(puntuacion)
                                .append(" puntos - ")
                                .append(fechaHora)
                                .append("\n");
                    }

                    rankingTextView.setText(rankingBuilder.toString());
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
