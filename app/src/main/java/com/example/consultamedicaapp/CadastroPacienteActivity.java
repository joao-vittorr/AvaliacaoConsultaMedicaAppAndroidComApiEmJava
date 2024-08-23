package com.example.consultamedicaapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class CadastroPacienteActivity extends AppCompatActivity {

    private EditText etNome, etCpf;
    private Button btnSave, btnDelete;
    private Paciente paciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_paciente);

        etNome = findViewById(R.id.etNome);
        etCpf = findViewById(R.id.etCpf);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        if (intent.hasExtra("paciente")) {
            paciente = (Paciente) intent.getSerializableExtra("paciente");
            etNome.setText(paciente.getNome());
            etCpf.setText(paciente.getCpf());
            btnDelete.setVisibility(View.VISIBLE);
        }

        // Adiciona o TextWatcher para aplicar a máscara ao CPF
        etCpf.addTextChangedListener(new CpfMask(etCpf));

        btnSave.setOnClickListener(v -> {
            if (paciente != null) {
                new UpdatePacienteTask().execute();
            } else {
                new CreatePacienteTask().execute();
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (paciente != null) {
                new DeletePacienteTask().execute();
            }
        });
    }


    private class CreatePacienteTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(getResources().getString(R.string.api_base_url) + "/pacientes");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonPaciente = new JSONObject();
                jsonPaciente.put("nome", etNome.getText().toString());
                jsonPaciente.put("cpf", etCpf.getText().toString());

                OutputStream os = conn.getOutputStream();
                os.write(jsonPaciente.toString().getBytes());
                os.flush();
                os.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return "Paciente criado com sucesso!";
                } else {
                    return "Erro ao criar paciente!";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Erro ao criar paciente!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(CadastroPacienteActivity.this, result, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class UpdatePacienteTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(getResources().getString(R.string.api_base_url) + "/pacientes/" + paciente.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonPaciente = new JSONObject();
                jsonPaciente.put("nome", etNome.getText().toString());
                jsonPaciente.put("cpf", etCpf.getText().toString());

                OutputStream os = conn.getOutputStream();
                os.write(jsonPaciente.toString().getBytes());
                os.flush();
                os.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return "Paciente atualizado com sucesso!";
                } else {
                    return "Erro ao atualizar paciente!";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Erro ao atualizar paciente!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(CadastroPacienteActivity.this, result, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class DeletePacienteTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(getResources().getString(R.string.api_base_url) + "/pacientes/" + paciente.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return "Paciente excluído com sucesso!";
                } else {
                    return "Erro ao excluir paciente!";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Erro ao excluir paciente!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(CadastroPacienteActivity.this, result, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
