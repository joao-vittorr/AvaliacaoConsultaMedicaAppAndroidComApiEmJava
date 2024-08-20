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

public class ListaPacientesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PacienteAdapter pacienteAdapter;
    private List<Paciente> pacienteList;
    String baseUrl = getResources().getString(R.string.api_base_url);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pacientes);

        recyclerView = findViewById(R.id.recyclerViewPacientes);
        progressBar = findViewById(R.id.progressBar);
        pacienteList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pacienteAdapter = new PacienteAdapter(pacienteList);
        recyclerView.setAdapter(pacienteAdapter);

        // Atualize a URL se necessário
        String apiUrl = baseUrl + "/pessoas"; // URL da API para buscar os pacientes

        // Carrega os dados da API
        new LoadPacientesTask().execute(apiUrl);
    }

    private class LoadPacientesTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
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

            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Paciente paciente = new Paciente();
                        paciente.setNome(jsonObject.getString("nome"));
                        paciente.setCpf(jsonObject.getString("cpf"));
                        pacienteList.add(paciente);
                    }
                    pacienteAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("ListaPacientesActivity", "Error parsing JSON", e);
                    Toast.makeText(ListaPacientesActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ListaPacientesActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
