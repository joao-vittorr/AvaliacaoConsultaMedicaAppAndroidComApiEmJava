package com.example.consultamedicaapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class ListaConsultasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ConsultaAdapter consultaAdapter;
    private List<Consulta> consultaList;
    private String baseUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_consultas);

        recyclerView = findViewById(R.id.recyclerViewConsultas);
        progressBar = findViewById(R.id.progressBar);
        consultaList = new ArrayList<>();

        // Obtenha o baseUrl após o setContentView
        baseUrl = getResources().getString(R.string.api_base_url) + "/consultas";

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        consultaAdapter = new ConsultaAdapter(consultaList);
        recyclerView.setAdapter(consultaAdapter);

        new LoadConsultasTask().execute(baseUrl);
    }

    private class LoadConsultasTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String apiUrl = strings[0];
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();
            } catch (Exception e) {
                Log.e("ListaConsultasActivity", "Error fetching data", e);
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    Log.e("ListaConsultasActivity", "Error closing reader", e);
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Consulta consulta = new Consulta();
                        consulta.setDescricao(jsonObject.getString("descricao"));
                        consulta.setMedico(jsonObject.getString("medico"));
                        consulta.setDataHora(jsonObject.getString("dataHora"));

                        // Parsing nested Paciente object
                        JSONObject pacienteJson = jsonObject.getJSONObject("paciente");
                        Paciente paciente = new Paciente();
                        paciente.setNome(pacienteJson.getString("nome"));
                        paciente.setCpf(pacienteJson.getString("cpf"));
                        consulta.setPaciente(paciente);

                        consultaList.add(consulta);
                    }
                    consultaAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("ListaConsultasActivity", "Error parsing JSON", e);
                    Toast.makeText(ListaConsultasActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ListaConsultasActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
