package com.example.consultamedicaapp;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditarConsultaActivity extends AppCompatActivity {

    private EditText editDescricao, editMedico, editDataHora, etPaciente;
    private Button btnSalvar;
    private Consulta consulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_consulta);

        editDescricao = findViewById(R.id.editDescricao);
        editMedico = findViewById(R.id.editMedico);
        editDataHora = findViewById(R.id.editDataHora);
        etPaciente = findViewById(R.id.etPaciente);
        btnSalvar = findViewById(R.id.btnSalvar);

        consulta = (Consulta) getIntent().getSerializableExtra("consulta");
        if (consulta != null) {
            editDescricao.setText(consulta.getDescricao());
            editMedico.setText(consulta.getMedico());
            editDataHora.setText(consulta.getDataHora());
            etPaciente.setText(consulta.getPaciente().getNome());
        }

        btnSalvar.setOnClickListener(v -> {
            consulta.setDescricao(editDescricao.getText().toString());
            consulta.setMedico(editMedico.getText().toString());
            consulta.setDataHora(editDataHora.getText().toString());
            // Aqui você pode atualizar o paciente conforme necessário

            // Chamar o método para atualizar a consulta na API
            atualizarConsultaNaAPI(consulta);
        });
    }

    private void atualizarConsultaNaAPI(Consulta consulta) {
        // Permite o uso de rede na thread principal, apenas para testes
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpURLConnection urlConnection = null;
        try {
            // Obter a URL base a partir dos recursos
            String baseUrl = getResources().getString(R.string.api_base_url);
            // Construir a URL completa para o endpoint de atualização
            URL url = new URL(baseUrl + "/consultas/" + consulta.getId());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

            // Criar o JSON com os dados da consulta
            String jsonInputString = "{"
                    + "\"descricao\":\"" + consulta.getDescricao() + "\","
                    + "\"medico\":\"" + consulta.getMedico() + "\","
                    + "\"dataHora\":\"" + consulta.getDataHora() + "\","
                    + "\"paciente\": {\"id\": \"" + consulta.getPaciente().getId() + "\"}"
                    + "}";

            // Enviar os dados para a API
            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = urlConnection.getResponseCode();
            if (code == 200) {
                // Sucesso
                Toast.makeText(EditarConsultaActivity.this, "Consulta atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                finish(); // Fechar a activity após a atualização
            } else {
                // Erro
                Toast.makeText(EditarConsultaActivity.this, "Erro ao atualizar consulta: " + code, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(EditarConsultaActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
