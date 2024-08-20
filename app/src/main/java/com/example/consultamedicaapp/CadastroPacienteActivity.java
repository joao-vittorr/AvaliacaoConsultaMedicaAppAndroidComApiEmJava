package com.example.consultamedicaapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CadastroPacienteActivity extends AppCompatActivity {

    private EditText etNome, etCpf;
    private Button btnSave, btnDelete;
    private Long pessoaId = null; // Identifica se é uma criação ou edição
    String baseUrl = getResources().getString(R.string.api_base_url);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_paciente);

        etNome = findViewById(R.id.etNome);
        etCpf = findViewById(R.id.etCpf);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Verifica se estamos editando um paciente
        if (getIntent().hasExtra("pessoaId")) {
            pessoaId = getIntent().getLongExtra("pessoaId", -1);
            etNome.setText(getIntent().getStringExtra("pessoaNome"));
            etCpf.setText(getIntent().getStringExtra("pessoaCpf"));
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etNome.getText()) || TextUtils.isEmpty(etCpf.getText())) {
                Toast.makeText(CadastroPacienteActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pessoaId != null) {
                new UpdatePessoaTask(pessoaId, etNome.getText().toString(), etCpf.getText().toString()).execute();
            } else {
                new CreatePessoaTask(etNome.getText().toString(), etCpf.getText().toString()).execute();
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (pessoaId != null) {
                new DeletePessoaTask(pessoaId).execute();
            }
        });
    }

    private class CreatePessoaTask extends AsyncTask<Void, Void, Boolean> {

        private String nome, cpf;

        CreatePessoaTask(String nome, String cpf) {
            this.nome = nome;
            this.cpf = cpf;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(baseUrl + "/pessoas");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setDoOutput(true);

                JSONObject pessoaJson = new JSONObject();
                pessoaJson.put("nome", nome);
                pessoaJson.put("cpf", cpf);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(pessoaJson.toString());
                writer.flush();
                writer.close();

                return connection.getResponseCode() == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CadastroPacienteActivity.this, "Pessoa criada com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CadastroPacienteActivity.this, "Erro ao criar pessoa", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdatePessoaTask extends AsyncTask<Void, Void, Boolean> {

        private Long id;
        private String nome, cpf;

        UpdatePessoaTask(Long id, String nome, String cpf) {
            this.id = id;
            this.nome = nome;
            this.cpf = cpf;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(baseUrl + "/pessoas/" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setDoOutput(true);

                JSONObject pessoaJson = new JSONObject();
                pessoaJson.put("nome", nome);
                pessoaJson.put("cpf", cpf);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(pessoaJson.toString());
                writer.flush();
                writer.close();

                return connection.getResponseCode() == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CadastroPacienteActivity.this, "Pessoa atualizada com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CadastroPacienteActivity.this, "Erro ao atualizar pessoa", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DeletePessoaTask extends AsyncTask<Void, Void, Boolean> {

        private Long id;

        DeletePessoaTask(Long id) {
            this.id = id;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(baseUrl + "/pessoas/" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");

                return connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CadastroPacienteActivity.this, "Pessoa deletada com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CadastroPacienteActivity.this, "Erro ao deletar pessoa", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
