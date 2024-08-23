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

public class ListarPacientesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PacienteAdapter pacienteAdapter;
    private List<Paciente> pacienteList;
    private String baseUrl;

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
                // Adicione lógica para editar o paciente
            }

            @Override
            public void onDeleteClick(Paciente paciente) {
                deletePaciente(paciente);
            }
        });

        recyclerView.setAdapter(pacienteAdapter);

        // Inicializando baseUrl dentro do onCreate
        baseUrl = getResources().getString(R.string.api_base_url);

        new LoadPacientesTask().execute(baseUrl + "/pacientes");
    }

    private class LoadPacientesTask extends AsyncTask<String, Void, String> {
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
                Log.e("ListaPacientesActivity", "Error fetching data", e);
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
                    Log.e("ListaPacientesActivity", "Error closing reader", e);
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
                        Paciente paciente = new Paciente();
                        paciente.setNome(jsonObject.getString("nome"));
                        paciente.setCpf(jsonObject.getString("cpf"));
                        paciente.setId(jsonObject.getInt("id")); // Adicione o ID
                        pacienteList.add(paciente);
                    }
                    pacienteAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("ListaPacientesActivity", "Error parsing JSON", e);
                    Toast.makeText(ListarPacientesActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ListarPacientesActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        }
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
                Log.e("ListaPacientesActivity", "Error deleting paciente", e);
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
}
