package com.example.consultamedicaapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CadastroPacienteActivity extends AppCompatActivity {

    private EditText etNome, etCpf;
    private Button btnSave, btnDelete, btnTakePhoto;
    private ImageView imageView;
    private Paciente paciente;
    private Bitmap capturedImage;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_paciente);

        etNome = findViewById(R.id.etNome);
        etCpf = findViewById(R.id.etCpf);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        if (intent.hasExtra("paciente")) {
            paciente = (Paciente) intent.getSerializableExtra("paciente");
            etNome.setText(paciente.getNome());
            etCpf.setText(paciente.getCpf());
            btnDelete.setVisibility(View.VISIBLE);
        }

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

        btnTakePhoto.setOnClickListener(this::takePhoto);
    }

    public void takePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            capturedImage = (Bitmap) extras.get("data");
            imageView.setImageBitmap(capturedImage);
        }
    }

    private class CreatePacienteTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(getResources().getString(R.string.api_base_url) + "/pacientes");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=*****");
                conn.setDoOutput(true);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                // Adiciona o nome
                dos.writeBytes("--*****\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"nome\"\r\n\r\n");
                dos.writeBytes(etNome.getText().toString());
                dos.writeBytes("\r\n");

                // Adiciona o CPF
                dos.writeBytes("--*****\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"cpf\"\r\n\r\n");
                dos.writeBytes(etCpf.getText().toString());
                dos.writeBytes("\r\n");

                // Adiciona a foto se estiver disponível
                if (capturedImage != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] imageBytes = bos.toByteArray();

                    dos.writeBytes("--*****\r\n");
                    dos.writeBytes("Content-Disposition: form-data; name=\"foto\"; filename=\"image.jpg\"\r\n");
                    dos.writeBytes("\r\n");
                    dos.write(imageBytes);
                    dos.writeBytes("\r\n");
                }

                dos.writeBytes("--*****--\r\n");

                dos.flush();
                dos.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                return content.toString();
            } catch (Exception e) {
                Log.e("CadastroPaciente", "Erro ao criar paciente", e);
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(CadastroPacienteActivity.this, "Paciente criado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CadastroPacienteActivity.this, "Erro ao criar paciente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdatePacienteTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(getResources().getString(R.string.api_base_url) + "/pacientes/" + paciente.getId());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=*****");
                conn.setDoOutput(true);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                // Adiciona o nome
                dos.writeBytes("--*****\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"nome\"\r\n\r\n");
                dos.writeBytes(etNome.getText().toString());
                dos.writeBytes("\r\n");

                // Adiciona o CPF
                dos.writeBytes("--*****\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"cpf\"\r\n\r\n");
                dos.writeBytes(etCpf.getText().toString());
                dos.writeBytes("\r\n");

                // Adiciona a foto se estiver disponível
                if (capturedImage != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] imageBytes = bos.toByteArray();

                    dos.writeBytes("--*****\r\n");
                    dos.writeBytes("Content-Disposition: form-data; name=\"foto\"; filename=\"image.jpg\"\r\n");
                    dos.writeBytes("\r\n");
                    dos.write(imageBytes);
                    dos.writeBytes("\r\n");
                }

                dos.writeBytes("--*****--\r\n");

                dos.flush();
                dos.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                return content.toString();
            } catch (Exception e) {
                Log.e("CadastroPaciente", "Erro ao atualizar paciente", e);
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(CadastroPacienteActivity.this, "Paciente atualizado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CadastroPacienteActivity.this, "Erro ao atualizar paciente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DeletePacienteTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(getResources().getString(R.string.api_base_url) + "/pacientes/" + paciente.getId());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    return "Success";
                } else {
                    return "Error";
                }
            } catch (Exception e) {
                Log.e("CadastroPaciente", "Erro ao deletar paciente", e);
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if ("Success".equals(result)) {
                Toast.makeText(CadastroPacienteActivity.this, "Paciente deletado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CadastroPacienteActivity.this, "Erro ao deletar paciente", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
