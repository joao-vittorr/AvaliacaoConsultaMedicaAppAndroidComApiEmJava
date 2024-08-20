package com.example.consultamedicaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConsultaAdapter extends RecyclerView.Adapter<ConsultaAdapter.ConsultaViewHolder> {

    private List<Consulta> consultas;

    public ConsultaAdapter(List<Consulta> consultas) {
        this.consultas = consultas;
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

        // Corrigido para exibir o nome do paciente
        if (consulta.getPaciente() != null) {
            holder.tvPaciente.setText(consulta.getPaciente().getNome());
        } else {
            holder.tvPaciente.setText("Paciente não informado");
        }
    }


    @Override
    public int getItemCount() {
        return consultas.size();
    }

    public static class ConsultaViewHolder extends RecyclerView.ViewHolder {

        TextView tvDescricao, tvMedico, tvDataHora, tvPaciente;

        public ConsultaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescricao = itemView.findViewById(R.id.tvDescricao);
            tvMedico = itemView.findViewById(R.id.tvMedico);
            tvDataHora = itemView.findViewById(R.id.tvDataHora);
            tvPaciente = itemView.findViewById(R.id.tvPaciente);
        }
    }
}
