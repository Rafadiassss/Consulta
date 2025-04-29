package com.example.consulta.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero; // Agora temos um campo número (representando o identificador)
    private String diagnostico;
    private String tratamento;
    private String observacoes;
    
    
    @OneToOne(mappedBy = "prontuario")
    private Consulta consulta;

    // Construtores
    public Prontuario() {}

    public Prontuario(String numero, String diagnostico, String tratamento, String observacoes) {
        this.numero = numero;
        this.diagnostico = diagnostico;
        this.tratamento = tratamento;
        this.observacoes = observacoes;
    }

    @JsonCreator
    public static Prontuario fromString(@JsonProperty("numero") String numero) {
        return new Prontuario(numero, "", "", "");  // Cria uma instância usando apenas o número
    }
    public Prontuario(String numero) {
        this.numero = numero;
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

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }


    // Método para gerar o relatório do prontuário
    public String gerarRelatorio() {
        return "Número: " + numero + "\nDiagnóstico: " + diagnostico + "\nTratamento: " + tratamento + "\nObservações: " + observacoes;
    }
}
