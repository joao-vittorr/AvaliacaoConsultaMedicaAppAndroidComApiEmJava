package com.example.consultamedicaapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;

public class CadastroConsultaActivity extends AppCompatActivity {

    private EditText etDescricao, etMedico, etDataHora, etCpf;
    private Spinner spinnerPacientes;
    private Button btnSalvarConsulta;
    private List<Paciente> pacienteList;
    private List<Paciente> filteredPacienteList;
    private Paciente selectedPaciente;
    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_consulta);

        etDescricao = findViewById(R.id.etDescricao);
        etMedico = findViewById(R.id.etMedico);
        etDataHora = findViewById(R.id.etDataHora);
        etCpf = findViewById(R.id.etCpf);
        spinnerPacientes = findViewById(R.id.spinnerPacientes);
        btnSalvarConsulta = findViewById(R.id.btnSalvarConsulta);

        pacienteList = new ArrayList<>();
        filteredPacienteList = new ArrayList<>();
        baseUrl = getResources().getString(R.string.api_base_url);

        // Carregar pacientes da API
        new LoadPacientesTask().execute(baseUrl + "/pessoas");

        // Configurar listener para o botão salvar
        btnSalvarConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarConsulta();
            }
        });

        // Configurar filtro de CPF
        etCpf.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String cpfPattern = "###.###.###-##";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    String formatted = "";

                    if (clean.length() > 0) {
                        formatted = clean.substring(0, Math.min(clean.length(), 3));
                    }
                    if (clean.length() > 3) {
                        formatted += "." + clean.substring(3, Math.min(clean.length(), 6));
                    }
                    if (clean.length() > 6) {
                        formatted += "." + clean.substring(6, Math.min(clean.length(), 9));
                    }
                    if (clean.length() > 9) {
                        formatted += "-" + clean.substring(9, Math.min(clean.length(), 11));
                    }

                    current = formatted;
                    etCpf.setText(formatted);
                    etCpf.setSelection(formatted.length());
                }
                filterPacientes(s.toString()); // Adiciona filtro durante a digitação
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configurar máscara de Data e Hora
        etDataHora.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    String formatted = "";

                    // Adiciona a máscara para data
                    if (clean.length() > 0) {
                        formatted = clean.substring(0, Math.min(clean.length(), 2));
                    }
                    if (clean.length() > 2) {
                        formatted += "/" + clean.substring(2, Math.min(clean.length(), 4));
                    }
                    if (clean.length() > 4) {
                        formatted += "/" + clean.substring(4, Math.min(clean.length(), 8));
                    }

                    // Adiciona a máscara para hora
                    if (clean.length() > 8) {
                        formatted += " " + clean.substring(8, Math.min(clean.length(), 10));
                    }
                    if (clean.length() > 10) {
                        formatted += ":" + clean.substring(10, Math.min(clean.length(), 12));
                    }

                    current = formatted;
                    etDataHora.setText(formatted);
                    etDataHora.setSelection(formatted.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
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

    private void filterPacientes(String cpf) {
        filteredPacienteList.clear();
        for (Paciente paciente : pacienteList) {
            String cleanCpf = paciente.getCpf().replaceAll("[^\\d]", "");
            String cleanInput = cpf.replaceAll("[^\\d]", "");
            if (cleanCpf.contains(cleanInput)) {
                filteredPacienteList.add(paciente);
            }
        }
        updateSpinner();
    }

    private void updateSpinner() {
        ArrayAdapter<Paciente> adapter = new ArrayAdapter<Paciente>(this, R.layout.spinner_item, filteredPacienteList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.spinner_item, parent, false);
                }
                TextView textView = convertView.findViewById(R.id.text1);
                Paciente paciente = getItem(position);
                textView.setText(paciente.getNome() + " - " + paciente.getCpf());
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.spinner_item, parent, false);
                }
                TextView textView = convertView.findViewById(R.id.text1);
                Paciente paciente = getItem(position);
                textView.setText(paciente.getNome() + " - " + paciente.getCpf());
                return convertView;
            }
        };

        spinnerPacientes.setAdapter(adapter);
        spinnerPacientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPaciente = filteredPacienteList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPaciente = null;
            }
        });
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
                    pacienteList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Paciente paciente = new Paciente();
                        paciente.setNome(jsonObject.getString("nome"));
                        paciente.setCpf(jsonObject.getString("cpf"));
                        pacienteList.add(paciente);
                    }
                    filteredPacienteList.addAll(pacienteList);
                    updateSpinner();

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
                URL url = new URL(baseUrl + "/consultas");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("descricao", consulta.getDescricao());
                jsonObject.put("medico", consulta.getMedico());
                jsonObject.put("dataHora", consulta.getDataHora());
                jsonObject.put("paciente", consulta.getPaciente().getId());

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                outputStreamWriter.write(jsonObject.toString());
                outputStreamWriter.flush();
                outputStreamWriter.close();

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
                finish();
            } else {
                Toast.makeText(CadastroConsultaActivity.this, "Erro ao salvar consulta", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
