package com.example.consultamedicaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class PacienteAdapter extends RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder> {

    private List<Paciente> pacientes;
    private OnPacienteClickListener listener;
    private Context context;

    public PacienteAdapter(List<Paciente> pacientes, OnPacienteClickListener listener) {
        this.pacientes = pacientes;
        this.listener = listener;
    }

    @Override
    public PacienteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_paciente, parent, false);
        return new PacienteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PacienteViewHolder holder, int position) {
        Paciente paciente = pacientes.get(position);
        holder.tvNome.setText(paciente.getNome());
        holder.tvCpf.setText(paciente.getCpf());

        String baseUrl = holder.itemView.getContext().getResources().getString(R.string.api_base_url); // Pega o baseUrl

        String fotoUrl = paciente.getFotoUrl(baseUrl);
        if (fotoUrl != null) {
            Glide.with(holder.itemView.getContext())
                    .load(fotoUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery) // Placeholder padrão
                    .error(android.R.drawable.ic_dialog_alert) // Imagem de erro padrão
                    .into(holder.imgFoto);
        } else {
            holder.imgFoto.setImageResource(R.drawable.default_avatar); // Define uma imagem padrão
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(paciente));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(paciente));
    }


    @Override
    public int getItemCount() {
        return pacientes.size();
    }

    public static class PacienteViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNome, tvCpf;
        public ImageView imgFoto;
        public ImageButton btnEdit, btnDelete;

        public PacienteViewHolder(View view) {
            super(view);
            tvNome = view.findViewById(R.id.tvNome);
            tvCpf = view.findViewById(R.id.tvCpf);
            imgFoto = view.findViewById(R.id.imgFoto);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }

    public interface OnPacienteClickListener {
        void onEditClick(Paciente paciente);
        void onDeleteClick(Paciente paciente);
    }
}
