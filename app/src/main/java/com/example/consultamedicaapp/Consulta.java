package com.example.consultamedicaapp;

import java.io.Serializable;

public class Consulta implements Serializable {
    private int id;
    private String descricao;
    private String medico;
    private String dataHora;
    private Paciente paciente;

    // Construtor padrão
    public Consulta() {
    }

    // Construtor com parâmetros
    public Consulta(int id, String descricao, String medico, String dataHora, Paciente paciente) {
        this.id = id;
        this.descricao = descricao;
        this.medico = medico;
        this.dataHora = dataHora;
        this.paciente = paciente;
    }

    // Getters e setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
}
