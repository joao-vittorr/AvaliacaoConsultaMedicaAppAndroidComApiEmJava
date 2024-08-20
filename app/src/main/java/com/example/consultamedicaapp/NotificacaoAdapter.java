package com.example.consultamedicaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificacaoAdapter extends RecyclerView.Adapter<NotificacaoAdapter.NotificacaoViewHolder> {

    private List<Notificacao> notificacoes;

    public NotificacaoAdapter(List<Notificacao> notificacoes) {
        this.notificacoes = notificacoes;
    }

    @NonNull
    @Override
    public NotificacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacao, parent, false);
        return new NotificacaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacaoViewHolder holder, int position) {
        Notificacao notificacao = notificacoes.get(position);
        holder.tvMensagem.setText(notificacao.getMensagem());
    }

    @Override
    public int getItemCount() {
        return notificacoes.size();
    }

    public static class NotificacaoViewHolder extends RecyclerView.ViewHolder {

        TextView tvMensagem;

        public NotificacaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensagem = itemView.findViewById(R.id.tvMensagem);
        }
    }
}
