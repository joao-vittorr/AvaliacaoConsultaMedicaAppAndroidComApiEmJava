package com.example.consultamedicaapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditarPacienteActivity extends AppCompatActivity {

    private EditText etNome, etCpf;
    private Button btnSalvar;
    private Paciente paciente;
    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_paciente);

        // Inicializa as views
        etNome = findViewById(R.id.etNome);
        etCpf = findViewById(R.id.etCpf);
        btnSalvar = findViewById(R.id.btnSalvar);

        // Recupera o paciente da intent
        paciente = (Paciente) getIntent().getSerializableExtra("paciente");
        baseUrl = getResources().getString(R.string.api_base_url);

        // Preenche os campos com os dados do paciente
        if (paciente != null) {
            etNome.setText(paciente.getNome());
            etCpf.setText(paciente.getCpf());
        }

        btnSalvar.setOnClickListener(v -> {
            String nome = etNome.getText().toString();
            String cpf = etCpf.getText().toString();

            if (nome.isEmpty() || cpf.isEmpty()) {
                Toast.makeText(EditarPacienteActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                paciente.setNome(nome);
                paciente.setCpf(cpf);
                new AtualizarPacienteTask().execute();
            }
        });
    }

    private class AtualizarPacienteTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(baseUrl + "/pacientes/" + paciente.getId());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nome", paciente.getNome());
                jsonObject.put("cpf", paciente.getCpf());

                OutputStream os = connection.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = connection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                Log.e("EditarPacienteActivity", "Error updating paciente", e);
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
            if (success) {
                Toast.makeText(EditarPacienteActivity.this, "Paciente atualizado com sucesso", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("resultado", "sucesso"); // Passa um indicador de sucesso
                setResult(RESULT_OK, resultIntent); // Define o resultado da atividade
                finish(); // Fecha a atividade
            } else {
                Toast.makeText(EditarPacienteActivity.this, "Erro ao atualizar paciente", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
