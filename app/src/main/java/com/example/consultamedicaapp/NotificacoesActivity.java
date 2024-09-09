package com.example.consultamedicaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificacoesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoNotifications;
    private NotificacaoAdapter notificacaoAdapter;
    private List<Notificacao> notificacaoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        recyclerView = findViewById(R.id.recyclerViewNotificacoes);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);

        notificacaoList = new ArrayList<>();

        // Adicione a lógica para buscar notificações da API
        // Aqui, vamos adicionar notificações de exemplo
        notificacaoList.add(new Notificacao("FALTOU FAZER O BACK ENVIAR A NOTIFICAÇÃO às 14:00"));
        notificacaoList.add(new Notificacao("Consulta com Dr. Silva às 14:00"));
        notificacaoList.add(new Notificacao("Consulta de retorno às 10:00 amanhã"));

        if (notificacaoList.isEmpty()) {
            tvNoNotifications.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoNotifications.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            notificacaoAdapter = new NotificacaoAdapter(notificacaoList);
            recyclerView.setAdapter(notificacaoAdapter);
        }
    }
}
