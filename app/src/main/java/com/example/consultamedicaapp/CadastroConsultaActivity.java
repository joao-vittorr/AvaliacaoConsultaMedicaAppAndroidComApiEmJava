package com.example.consultamedicaapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CadastroConsultaActivity extends AppCompatActivity {

    private EditText etDescricao, etMedico, etDataHora;
    private Spinner spinnerPacientes;
    private Button btnSalvarConsulta;
    private List<Paciente> pacienteList;
    private Paciente selectedPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_consulta);

        etDescricao = findViewById(R.id.etDescricao);
        etMedico = findViewById(R.id.etMedico);
        etDataHora = findViewById(R.id.etDataHora);
        spinnerPacientes = findViewById(R.id.spinnerPacientes);
        btnSalvarConsulta = findViewById(R.id.btnSalvarConsulta);

        pacienteList = new ArrayList<>();

        // Carregar pacientes da API
        new LoadPacientesTask().execute("http://192.168.3.2:8080/pessoas");

        // Configurar listener para o botão salvar
        btnSalvarConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarConsulta();
            }
        });
    }

    private void salvarConsulta() {
        String descricao = etDescricao.getText().toString();
        String medico = etMedico.getText().toString();
        String dataHora = etDataHora.getText().toString();

        if (selectedPaciente != null) {
            Consulta consulta = new Consulta();
            consulta.setDescricao(descricao);
            consulta.setMedico(medico);
            consulta.setDataHora(dataHora);
            consulta.setPaciente(selectedPaciente);

            // Enviar a consulta para a API
            new SaveConsultaTask().execute(consulta);
        } else {
            Toast.makeText(this, "Por favor, selecione um paciente", Toast.LENGTH_SHORT).show();
        }
    }

    private class LoadPacientesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Paciente paciente = new Paciente();
                        paciente.setNome(jsonObject.getString("nome"));
                        paciente.setCpf(jsonObject.getString("cpf"));
                        pacienteList.add(paciente);
                    }

                    // Configurar o spinner com os pacientes
                    ArrayAdapter<Paciente> adapter = new ArrayAdapter<>(CadastroConsultaActivity.this, android.R.layout.simple_spinner_item, pacienteList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPacientes.setAdapter(adapter);
                    spinnerPacientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedPaciente = pacienteList.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedPaciente = null;
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CadastroConsultaActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CadastroConsultaActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SaveConsultaTask extends AsyncTask<Consulta, Void, String> {
        @Override
        protected String doInBackground(Consulta... consultas) {
            Consulta consulta = consultas[0];
            try {
                URL url = new URL("http://192.168.3.2:8080/consultas");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setDoOutput(true);

                JSONObject consultaJson = new JSONObject();
                consultaJson.put("descricao", consulta.getDescricao());
                consultaJson.put("medico", consulta.getMedico());
                consultaJson.put("dataHora", consulta.getDataHora());

                JSONObject pacienteJson = new JSONObject();
                pacienteJson.put("cpf", consulta.getPaciente().getCpf());
                consultaJson.put("paciente", pacienteJson);

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(consultaJson.toString());
                out.close();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                Toast.makeText(CadastroConsultaActivity.this, "Consulta salva com sucesso", Toast.LENGTH_SHORT).show();
                finish(); // Finaliza a Activity após o cadastro
            } else {
                Toast.makeText(CadastroConsultaActivity.this, "Erro ao salvar consulta", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
