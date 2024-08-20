package com.example.consultamedicaapp;

public class Notificacao {
    private String mensagem;

    public Notificacao(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
