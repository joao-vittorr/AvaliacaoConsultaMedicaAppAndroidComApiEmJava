package com.example.consultamedicaapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConsultaAdapter extends RecyclerView.Adapter<ConsultaAdapter.ConsultaViewHolder> {

    private List<Consulta> consultas;
    private Context context;
    private String baseUrl;

    public ConsultaAdapter(Context context, List<Consulta> consultas) {
        this.context = context;
        this.consultas = consultas;
        this.baseUrl = context.getResources().getString(R.string.api_base_url); // Inicialize baseUrl aqui
    }

    @NonNull
    @Override
    public ConsultaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consulta, parent, false);
        return new ConsultaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultaViewHolder holder, int position) {
        Consulta consulta = consultas.get(position);
        holder.tvDescricao.setText(consulta.getDescricao());
        holder.tvMedico.setText(consulta.getMedico());
        holder.tvDataHora.setText(consulta.getDataHora());

        if (consulta.getPaciente() != null) {
            holder.tvPaciente.setText(consulta.getPaciente().getNome());
        } else {
            holder.tvPaciente.setText("Paciente não informado");
        }

        // Botão Editar
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarConsultaActivity.class);
            intent.putExtra("consulta", consulta);
            context.startActivity(intent);
        });

        // Botão Excluir
        holder.btnExcluir.setOnClickListener(v -> excluirConsulta(consulta, position));
    }

    @Override
    public int getItemCount() {
        return consultas.size();
    }

    public static class ConsultaViewHolder extends RecyclerView.ViewHolder {

        TextView tvDescricao, tvMedico, tvDataHora, tvPaciente;
        Button btnEditar, btnExcluir;

        public ConsultaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescricao = itemView.findViewById(R.id.tvDescricao);
            tvMedico = itemView.findViewById(R.id.tvMedico);
            tvDataHora = itemView.findViewById(R.id.tvDataHora);
            tvPaciente = itemView.findViewById(R.id.tvPaciente);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }

    private void excluirConsulta(Consulta consulta, int position) {
        // Remover a consulta da lista localmente
        consultas.remove(position);
        notifyItemRemoved(position);

        // Fazer uma chamada à API para excluir a consulta do servidor
        OkHttpClient client = new OkHttpClient();
        String url = baseUrl + "/consultas/" + consulta.getId(); // Usa o ID como int e o converte para string
        Log.d("ConsultaAdapter", "URL de exclusão: " + url);

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("ConsultaAdapter", "Falha na requisição de exclusão: " + e.getMessage());
                ((ListaConsultasActivity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Falha ao excluir a consulta.", Toast.LENGTH_SHORT).show();
                    consultas.add(position, consulta);
                    notifyItemInserted(position);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    Log.e("ConsultaAdapter", "Erro na resposta de exclusão: Código " + response.code() + ", Mensagem: " + errorBody);
                    ((ListaConsultasActivity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Erro ao excluir consulta do servidor.", Toast.LENGTH_SHORT).show();
                        consultas.add(position, consulta);
                        notifyItemInserted(position);
                    });
                } else {
                    ((ListaConsultasActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Consulta excluída com sucesso.", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
