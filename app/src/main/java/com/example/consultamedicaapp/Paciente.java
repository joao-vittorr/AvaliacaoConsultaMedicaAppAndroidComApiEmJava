package com.example.consultamedicaapp;

import java.io.Serializable;

public class Paciente implements Serializable {
    private int id; // Alterado de String para int
    private String nome;
    private String cpf;

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return nome + " - " + cpf;
    }
}
