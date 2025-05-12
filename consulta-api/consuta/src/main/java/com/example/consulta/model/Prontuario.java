package com.example.consulta.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
@Entity
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private String diagnostico;
    private String tratamento;
    private String observacoes;

    @OneToOne(mappedBy = "prontuario")
    private Consulta consulta;  // Relacionamento bidirecional

    private String prontuario;  // Aqui estamos tratando como uma string

    // Construtores
    public Prontuario() {}

    public Prontuario(String numero, String diagnostico, String tratamento, String observacoes) {
        this.numero = numero;
        this.diagnostico = diagnostico;
        this.tratamento = tratamento;
        this.observacoes = observacoes;
    }

    // Construtor com @JsonCreator para deserializar a string
    @JsonCreator
    public Prontuario(@JsonProperty("prontuario") String prontuario) {
        this.prontuario = prontuario;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamento() {
        return tratamento;
    }

    public void setTratamento(String tratamento) {
        this.tratamento = tratamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getProntuario() {
        return prontuario;
    }

    public void setProntuario(String prontuario) {
        this.prontuario = prontuario;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
}