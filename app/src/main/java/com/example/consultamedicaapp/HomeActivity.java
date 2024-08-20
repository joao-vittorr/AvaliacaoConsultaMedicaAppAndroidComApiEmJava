package com.example.consultamedicaapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

        // Configurar a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerViewConsultas);
        progressBar = findViewById(R.id.progressBar);
        consultaList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        consultaAdapter = new ConsultaAdapter(consultaList);
        recyclerView.setAdapter(consultaAdapter);

        // Atualize a URL se necessário
        String apiUrl = "http://192.168.3.2:8080/consultas"; // Para dispositivo físico

        // Carrega os dados da API
        new LoadConsultasTask().execute(apiUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_consulta:
                startActivity(new Intent(this, CadastroConsultaActivity.class));
                return true;
            case R.id.action_add_paciente:
                startActivity(new Intent(this, ListaPacientesActivity.class));
                return true;
            case R.id.action_notifications:
                startActivity(new Intent(this, NotificacoesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                Log.e("HomeActivity", "Error fetching data", e);
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
                    Log.e("HomeActivity", "Error closing reader", e);
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
                    Toast.makeText(HomeActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(HomeActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
