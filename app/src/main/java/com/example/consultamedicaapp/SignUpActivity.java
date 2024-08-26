package com.example.consultamedicaapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnSignUp;
    private ProgressDialog progressDialog;
    private String baseUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Inicializa o baseUrl com o valor definido no arquivo strings.xml
        baseUrl = getResources().getString(R.string.api_base_url);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
                } else {
                    new SignUpTask(username, password).execute();
                }
            }
        });
    }

    private class SignUpTask extends AsyncTask<Void, Void, String> {

        private String username;
        private String password;

        SignUpTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SignUpActivity.this, "Aguarde", "Cadastrando...", true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(baseUrl + "/api/auth/signup");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);

                String jsonInputString = new JSONObject()
                        .put("username", username)
                        .put("password", password)
                        .toString();

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = urlConnection.getResponseCode();
                InputStreamReader inputStreamReader;
                if (code == HttpURLConnection.HTTP_CREATED) {
                    inputStreamReader = new InputStreamReader(urlConnection.getInputStream(), "utf-8");
                } else {
                    inputStreamReader = new InputStreamReader(urlConnection.getErrorStream(), "utf-8");
                }

                BufferedReader br = new BufferedReader(inputStreamReader);
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                result = response.toString();
            } catch (Exception e) {
                result = "Error: " + e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result.contains("User registered successfully")) {
                Toast.makeText(SignUpActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                // Navega para a tela de login
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();  // Finaliza a SignUpActivity para que o usuário não possa voltar a ela
            } else {
                Toast.makeText(SignUpActivity.this, "Erro ao cadastrar, tente novamente: " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
