package com.example.consultamedicaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnAddConsulta;
    private Button btnListPaciente;
    private Button btnAddPaciente;
    private Button btnListConsultas;
    private Button btnNotifications;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnAddConsulta = findViewById(R.id.btnAddConsulta);
        btnListPaciente = findViewById(R.id.btnListPaciente);
        btnListConsultas = findViewById(R.id.btnListConsultas);
        btnAddPaciente = findViewById(R.id.btnAddPaciente);
        btnNotifications = findViewById(R.id.btnNotifications);

        btnAddConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CadastroConsultaActivity.class));
            }
        });

        btnListConsultas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ListaConsultasActivity.class));
            }
        });

        btnListPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ListaPacientesActivity.class));
            }
        });

        btnAddPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CadastroPacienteActivity.class));
            }
        });


        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, NotificacoesActivity.class));
            }
        });
    }
}
