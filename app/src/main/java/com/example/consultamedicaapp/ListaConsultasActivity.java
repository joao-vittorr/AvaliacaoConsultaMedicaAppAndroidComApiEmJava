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

        baseUrl = getResources().getString(R.string.api_base_url) + "/consultas";

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        consultaAdapter = new ConsultaAdapter(this, consultaList);
        recyclerView.setAdapter(consultaAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarConsultas();
    }

    private void carregarConsultas() {
        consultaList.clear();  // Limpar a lista para evitar duplicação
        consultaAdapter.notifyDataSetChanged(); // Notificar o adapter que a lista foi limpa
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

                        consulta.setId(jsonObject.getInt("id"));
                        consulta.setDescricao(jsonObject.getString("descricao"));
                        consulta.setMedico(jsonObject.getString("medico"));
                        consulta.setDataHora(jsonObject.getString("dataHora"));

                        if (jsonObject.has("paciente") && !jsonObject.isNull("paciente")) {
                            JSONObject pacienteObject = jsonObject.getJSONObject("paciente");
                            Paciente paciente = new Paciente();
                            paciente.setId(pacienteObject.getInt("id"));
                            paciente.setNome(pacienteObject.getString("nome"));
                            consulta.setPaciente(paciente);
                        }

                        consultaList.add(consulta);
                    }
                    consultaAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(ListaConsultasActivity.this, "Erro ao processar os dados da consulta", Toast.LENGTH_SHORT).show();
                    Log.e("ListaConsultasActivity", "JSON parsing error", e);
                }
            } else {
                Toast.makeText(ListaConsultasActivity.this, "Falha ao carregar as consultas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
