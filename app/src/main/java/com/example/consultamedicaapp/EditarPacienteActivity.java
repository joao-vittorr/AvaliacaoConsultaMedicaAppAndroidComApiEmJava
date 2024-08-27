package com.example.consultamedicaapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditarPacienteActivity extends AppCompatActivity {

    private EditText etNome, etCpf;
    private Button btnSalvar, btnTirarFoto;
    private ImageView ivFoto;
    private Paciente paciente;
    private String baseUrl;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_paciente);

        // Inicializa as views
        etNome = findViewById(R.id.etNome);
        etCpf = findViewById(R.id.etCpf);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnTirarFoto = findViewById(R.id.btnFoto);
        ivFoto = findViewById(R.id.imgFoto);

        // Recupera o paciente da intent
        paciente = (Paciente) getIntent().getSerializableExtra("paciente");
        baseUrl = getResources().getString(R.string.api_base_url);

        // Preenche os campos com os dados do paciente
        if (paciente != null) {
            etNome.setText(paciente.getNome());
            etCpf.setText(paciente.getCpf());
            Glide.with(this)
                    .load(paciente.getFotoUrl(baseUrl)) // Ajuste conforme o campo da URL da foto
                    .into(ivFoto);
        }

        // Adiciona o TextWatcher para aplicar a máscara ao CPF
        etCpf.addTextChangedListener(new CpfMask(etCpf));

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

        btnTirarFoto.setOnClickListener(v -> checkPermissions());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        getString(R.string.file_provider_authority),
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ivFoto.setImageURI(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permissão necessária para tirar fotos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static final String BOUNDARY = "*****";

    private class AtualizarPacienteTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            try {
                URL url = new URL(baseUrl + "/pacientes/" + paciente.getId());
                Log.d("EditarPacienteActivity", "Request URL: " + url.toString());  // Adicione este log
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                conn.setDoOutput(true);

                dos = new DataOutputStream(conn.getOutputStream());

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
                if (photoUri != null) {
                    InputStream inputStream = getContentResolver().openInputStream(photoUri);

                    // Compressão da imagem
                    Bitmap capturedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    capturedImage.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    byte[] imageData = bos.toByteArray();
                    dos.writeBytes("--" + BOUNDARY + "\r\n");
                    dos.writeBytes("Content-Disposition: form-data; name=\"foto\"; filename=\"" + new File(photoUri.getPath()).getName() + "\"\r\n");
                    dos.writeBytes("Content-Type: image/jpeg\r\n");
                    dos.writeBytes("\r\n");

                    dos.write(imageData);
                    dos.writeBytes("\r\n");

                    inputStream.close();
                }

                dos.writeBytes("--" + BOUNDARY + "--\r\n");

                dos.flush();

                int responseCode = conn.getResponseCode();
                Log.d("EditarPacienteActivity", "Response Code: " + responseCode);  // Adicione este log

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                is.close();

                Log.d("EditarPacienteActivity", "Response Body: " + response.toString());  // Adicione este log

                return responseCode == HttpURLConnection.HTTP_OK ? "Paciente atualizado com sucesso" : "Falha ao atualizar paciente";

            } catch (Exception e) {
                e.printStackTrace();
                return "Erro ao atualizar paciente: " + e.getMessage();
            } finally {
                if (dos != null) {
                    try {
                        dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(EditarPacienteActivity.this, result, Toast.LENGTH_SHORT).show();
            if (result.contains("sucesso")) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
