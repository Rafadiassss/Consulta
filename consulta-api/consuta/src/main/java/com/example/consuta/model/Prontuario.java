package com.example.consuta.model;

import jakarta.persistence.*;

@Entity
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diagnostico;
    private String tratamento;
    private String observacoes;

    @OneToOne(mappedBy = "prontuario")
    private Consulta consulta;

    // Construtores
    public Prontuario() {}

    public Prontuario(String diagnostico, String tratamento, String observacoes) {
        this.diagnostico = diagnostico;
        this.tratamento = tratamento;
        this.observacoes = observacoes;
    }

    // Getters e Setters
    public Long getId() {
        return id;
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

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    // Método para gerar o relatório do prontuário
    public String gerarRelatorio() {
        return "Diagnóstico: " + diagnostico + "\nTratamento: " + tratamento + "\nObservações: " + observacoes;
    }
}
