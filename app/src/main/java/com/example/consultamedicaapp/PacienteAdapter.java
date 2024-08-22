package com.example.consultamedicaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PacienteAdapter extends RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder> {

    private List<Paciente> pacientes;

    public PacienteAdapter(List<Paciente> pacientes) {
        this.pacientes = pacientes;
    }

    @NonNull
    @Override
    public PacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paciente, parent, false);
        return new PacienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PacienteViewHolder holder, int position) {
        Paciente paciente = pacientes.get(position);
        holder.tvNome.setText(paciente.getNome());
        holder.tvCpf.setText(paciente.getCpf());
    }

    @Override
    public int getItemCount() {
        return pacientes.size();
    }

    public static class PacienteViewHolder extends RecyclerView.ViewHolder {

        TextView tvNome, tvCpf;

        public PacienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNome);
            tvCpf = itemView.findViewById(R.id.tvCpf);
        }
    }
}