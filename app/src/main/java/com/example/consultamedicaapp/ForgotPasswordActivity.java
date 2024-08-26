package com.example.consultamedicaapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                if (!email.isEmpty()) {
                    // Inicia a tarefa assíncrona para solicitar a recuperação da senha
                    new ForgotPasswordTask(email).execute();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Por favor, insira seu email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Classe AsyncTask para realizar a operação de recuperação de senha em segundo plano
    private class ForgotPasswordTask extends AsyncTask<Void, Void, Boolean> {

        private String email;

        public ForgotPasswordTask(String email) {
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Recupera a base URL dos recursos
                String baseUrl = getResources().getString(R.string.api_base_url);
                // Constrói a URL completa para o endpoint de recuperação de senha
                URL url = new URL(baseUrl + "/api/forgot-password");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Cria o JSON com o email do usuário
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);

                // Envia os dados para a API
                OutputStream os = connection.getOutputStream();
                byte[] input = jsonParam.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
                os.close();

                // Verifica o código de resposta da API
                int responseCode = connection.getResponseCode();
                connection.disconnect();

                // Retorna true se a resposta for 200 OK
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(ForgotPasswordActivity.this, "Email de recuperação enviado com sucesso!", Toast.LENGTH_SHORT).show();
                finish(); // Fecha a activity de recuperação de senha
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "Erro ao enviar email de recuperação. Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
