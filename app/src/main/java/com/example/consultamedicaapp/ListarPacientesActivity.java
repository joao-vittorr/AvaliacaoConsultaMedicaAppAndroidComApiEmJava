package com.example.consultamedicaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class ListarPacientesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PacienteAdapter pacienteAdapter;
    private List<Paciente> pacienteList;
    private String baseUrl;
    private static final int EDITAR_PACIENTE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_pacientes);

        recyclerView = findViewById(R.id.recyclerViewPacientes);
        progressBar = findViewById(R.id.progressBar);
        pacienteList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pacienteAdapter = new PacienteAdapter(pacienteList, new PacienteAdapter.OnPacienteClickListener() {
            @Override
            public void onEditClick(Paciente paciente) {
                Intent intent = new Intent(ListarPacientesActivity.this, EditarPacienteActivity.class);
                intent.putExtra("paciente", paciente);
                startActivityForResult(intent, EDITAR_PACIENTE_REQUEST_CODE); // Solicita um resultado da atividade
            }

            @Override
            public void onDeleteClick(Paciente paciente) {
                deletePaciente(paciente);
            }
        });

        recyclerView.setAdapter(pacienteAdapter);

        // Inicializando baseUrl dentro do onCreate
        baseUrl = getResources().getString(R.string.api_base_url);

        carregarPacientes(); // Carrega a lista de pacientes
    }

    @SuppressLint("StaticFieldLeak")
    private void carregarPacientes() {
        new AsyncTask<Void, Void, List<Paciente>>() {
            @Override
            protected List<Paciente> doInBackground(Void... voids) {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                List<Paciente> pacientes = new ArrayList<>();
                try {
                    URL url = new URL(baseUrl + "/pacientes");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    JSONArray jsonArray = new JSONArray(result.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Paciente paciente = new Paciente();
                        paciente.setNome(jsonObject.getString("nome"));
                        paciente.setCpf(jsonObject.getString("cpf"));
                        paciente.setId(jsonObject.getInt("id")); // Adiciona o ID
                        paciente.setFoto(jsonObject.getString("foto")); // Adiciona o campo foto
                        pacientes.add(paciente);
                    }
                } catch (Exception e) {
                    Log.e("ListarPacientesActivity", "Error fetching data", e);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (Exception e) {
                        Log.e("ListarPacientesActivity", "Error closing reader", e);
                    }
                }
                return pacientes;
            }

            @Override
            protected void onPostExecute(List<Paciente> pacientes) {
                super.onPostExecute(pacientes);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (pacientes != null) {
                    pacienteList.clear();
                    pacienteList.addAll(pacientes);
                    pacienteAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ListarPacientesActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    private void deletePaciente(Paciente paciente) {
        new DeletePacienteTask(paciente).execute();
    }

    private class DeletePacienteTask extends AsyncTask<Void, Void, Boolean> {
        private Paciente paciente;

        public DeletePacienteTask(Paciente paciente) {
            this.paciente = paciente;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(baseUrl + "/pacientes/" + paciente.getId());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.connect();

                int responseCode = connection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_NO_CONTENT;
            } catch (Exception e) {
                Log.e("ListarPacientesActivity", "Error deleting paciente", e);
                return false;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            progressBar.setVisibility(View.GONE);
            if (success) {
                pacienteList.removeIf(p -> p.getId() == paciente.getId()); // Remove o paciente da lista
                pacienteAdapter.notifyDataSetChanged();
                Toast.makeText(ListarPacientesActivity.this, "Paciente excluído com sucesso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ListarPacientesActivity.this, "Erro ao excluir paciente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDITAR_PACIENTE_REQUEST_CODE && resultCode == RESULT_OK) {
            carregarPacientes(); // Atualiza a lista de pacientes
        }
    }
}

