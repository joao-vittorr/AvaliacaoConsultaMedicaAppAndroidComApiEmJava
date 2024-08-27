package com.example.consultamedicaapp;

import java.io.Serializable;

public class Paciente implements Serializable {
    private int id;
    private String nome;
    private String cpf;
    private String foto;

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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFotoUrl(String baseUrl) {
        return baseUrl + "/uploads/" + foto;
    }
}

