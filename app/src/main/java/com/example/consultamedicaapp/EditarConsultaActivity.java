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

    private EditText editDescricao, editMedico, editDataHora;
    private Button btnSalvar;
    private Consulta consulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_consulta);

        editDescricao = findViewById(R.id.editDescricao);
        editMedico = findViewById(R.id.editMedico);
        editDataHora = findViewById(R.id.editDataHora);
        btnSalvar = findViewById(R.id.btnSalvar);

        consulta = (Consulta) getIntent().getSerializableExtra("consulta");
        if (consulta != null) {
            editDescricao.setText(consulta.getDescricao());
            editMedico.setText(consulta.getMedico());
            editDataHora.setText(consulta.getDataHora());
        }

        btnSalvar.setOnClickListener(v -> {
            consulta.setDescricao(editDescricao.getText().toString());
            consulta.setMedico(editMedico.getText().toString());
            consulta.setDataHora(editDataHora.getText().toString());

            // Chamar o método para atualizar a consulta na API
            atualizarConsultaNaAPI(consulta);
        });
    }

    private void atualizarConsultaNaAPI(Consulta consulta) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpURLConnection urlConnection = null;
        try {
            String baseUrl = getResources().getString(R.string.api_base_url);
            URL url = new URL(baseUrl + "/consultas/" + consulta.getId());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

            String jsonInputString = "{"
                    + "\"descricao\":\"" + consulta.getDescricao() + "\","
                    + "\"medico\":\"" + consulta.getMedico() + "\","
                    + "\"dataHora\":\"" + consulta.getDataHora() + "\""
                    + "}";

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = urlConnection.getResponseCode();
            if (code == 200) {
                Toast.makeText(EditarConsultaActivity.this, "Consulta atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
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
