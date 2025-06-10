package com.example.consulta.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
    private Consulta consulta;  // Relacionamento bidirecional

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Usuario medico;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Usuario paciente;
    // Construtor padrão
    public Prontuario() {}

    // Construtor completo
    public Prontuario(String numero, String diagnostico, String tratamento, String observacoes) {
        this.numero = numero;
        this.diagnostico = diagnostico;
        this.tratamento = tratamento;
        this.observacoes = observacoes;
    }

    // Getters e Setters
    
    public Long getId() {
        return id;
    }

    public Usuario getMedico() {
        return medico;
    }

    public void setMedico(Usuario medico) {
        this.medico = medico;
    }

    public Usuario getPaciente() {
        return paciente;
    }

    public void setPaciente(Usuario paciente) {
        this.paciente = paciente;
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
}
