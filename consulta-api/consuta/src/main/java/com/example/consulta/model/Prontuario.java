package com.example.consulta.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime data;

    private String status;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    @JsonIgnoreProperties("consultas")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    @JsonIgnoreProperties("consultas")
    private Medico medico;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pagamento_id")
    private Pagamento pagamento;

    @OneToOne(cascade = CascadeType.PERSIST) // Usando CascadeType.PERSIST para garantir persistência do prontuário
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    private String nomePontuario;

    // Construtores
    public Prontuario() {
    }

    public Prontuario(String nomePontuario) {
        this.nomePontuario = nomePontuario;
    }

    public Prontuario(LocalDateTime data, String status, Paciente paciente, Medico medico) {
        this.data = data;
        this.status = status;
        this.paciente = paciente;
        this.medico = medico;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Consulta getProntuario() {
        return consulta;
    }

    public void setProntuario(Consulta consulta) {
        this.consulta = consulta;
        
    }

    public String getNomePontuario() {
        return nomePontuario;
    }

    public void setNomeConsulta(String nomePontuario) {
        this.nomePontuario = nomePontuario;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

}