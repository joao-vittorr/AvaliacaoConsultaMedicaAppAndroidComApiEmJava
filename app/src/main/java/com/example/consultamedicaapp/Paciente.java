package com.example.consultamedicaapp;

import java.io.Serializable;

public class Paciente implements Serializable {

    private int id;
    private String nome;
    private String cpf;

    // Construtor padrão
    public Paciente() {
    }

    // Construtor com parâmetros
    public Paciente(int id, String nome, String cpf) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
    }

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
}
