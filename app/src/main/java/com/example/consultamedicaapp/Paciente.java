package com.example.consultamedicaapp;

public class Paciente {
    private String nome;
    private String cpf;

    // Getters and setters
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
        return nome; // Exibe o nome do paciente no Spinner
    }
}

