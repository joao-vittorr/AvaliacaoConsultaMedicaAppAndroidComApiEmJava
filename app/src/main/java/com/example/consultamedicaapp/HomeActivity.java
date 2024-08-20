package com.example.consultamedicaapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ConsultaAdapter consultaAdapter;
    private List<Consulta> consultaList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerViewConsultas);
        progressBar = findViewById(R.id.progressBar);
        consultaList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        consultaAdapter = new ConsultaAdapter(consultaList);
        recyclerView.setAdapter(consultaAdapter);

        // Carrega os dados da API
        new LoadConsultasTask().execute("http://localhost:8080/consultas");
    }

    private class LoadConsultasTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String apiUrl = strings[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();
            } catch (Exception e) {
                Log.e("HomeActivity", "Error fetching data", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);

            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Consulta consulta = new Consulta();
                        consulta.setDescricao(jsonObject.getString("descricao"));
                        consulta.setMedico(jsonObject.getString("medico"));
                        consulta.setDataHora(jsonObject.getString("dataHora"));
                        consulta.setPaciente(jsonObject.getJSONObject("paciente").getString("nome"));
                        consultaList.add(consulta);
                    }
                    consultaAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("HomeActivity", "Error parsing JSON", e);
                }
            }
        }
    }
}
