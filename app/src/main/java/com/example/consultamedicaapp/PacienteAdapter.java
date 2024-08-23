package com.example.consultamedicaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PacienteAdapter extends RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder> {

    private List<Paciente> pacientes;
    private OnPacienteClickListener listener;

    public PacienteAdapter(List<Paciente> pacientes, OnPacienteClickListener listener) {
        this.pacientes = pacientes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paciente, parent, false);
        return new PacienteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PacienteViewHolder holder, int position) {
        Paciente paciente = pacientes.get(position);
        holder.tvNome.setText(paciente.getNome());
        holder.tvCpf.setText(paciente.getCpf());
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(paciente));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(paciente));
    }

    @Override
    public int getItemCount() {
        return pacientes.size();
    }

    public static class PacienteViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNome, tvCpf;
        public ImageButton btnEdit, btnDelete;

        public PacienteViewHolder(View view) {
            super(view);
            tvNome = view.findViewById(R.id.tvNome);
            tvCpf = view.findViewById(R.id.tvCpf);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }

    public interface OnPacienteClickListener {
        void onEditClick(Paciente paciente);
        void onDeleteClick(Paciente paciente);
    }
}

