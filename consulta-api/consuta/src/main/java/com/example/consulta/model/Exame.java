package com.example.consulta.model;

import jakarta.persistence.*;

@Entity
public class Exame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String resultado;
    private String observacoes;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "consulta_id")
    private Prontuario Prontuario;

    // Construtores
    public Exame() {
    }

    public Exame(String nome, String resultado, String observacoes, Prontuario Prontuario) {
        this.nome = nome;
        this.resultado = resultado;
        this.observacoes = observacoes;
        this.Prontuario = Prontuario;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Prontuario getConsulta() {
        return Prontuario;
    }

    public void setConsulta(Prontuario consulta) {
        this.Prontuario = consulta;
    }
}