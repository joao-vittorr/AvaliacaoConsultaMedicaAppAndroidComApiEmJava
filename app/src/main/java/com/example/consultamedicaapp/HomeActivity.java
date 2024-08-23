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

        // Inicialização dos botões
        btnAddConsulta = findViewById(R.id.btnAddConsulta);
        btnListPaciente = findViewById(R.id.btnListPaciente);
        btnAddPaciente = findViewById(R.id.btnAddPaciente);
        btnListConsultas = findViewById(R.id.btnListConsultas);
        btnNotifications = findViewById(R.id.btnNotifications);

        // Configuração do botão de adicionar consulta
        btnAddConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CadastroConsultaActivity.class));
            }
        });

        // Configuração do botão para listar consultas
        btnListConsultas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ListaConsultasActivity.class));
            }
        });

        // Configuração do botão para listar pacientes
        btnListPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ListarPacientesActivity.class));
            }
        });

        // Configuração do botão de adicionar paciente
        btnAddPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CadastroPacienteActivity.class));
            }
        });

        // Configuração do botão de notificações
        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, NotificacoesActivity.class));
            }
        });
    }
}
