package com.example.consulta.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@DiscriminatorValue("PACIENTE") // O valor na coluna 'dtype' para um paciente
public class Paciente extends Usuario {

    private String cpf;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prontuario> prontuarios;
    @Column(name = "cartao_sus")
    private String cartaoSus;

    // Construtores
    public Paciente() {
    }

    public Paciente(String nome, String cpf, LocalDate dataNascimento, String usuario, String senha, String cartaoSus) {
        this.cartaoSus = cartaoSus;
        this.cpf = cpf;
    }

    // Getters e Setters
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public List<Prontuario> getConsultas() {
        return prontuarios;
    }

    public void setConsultas(List<Prontuario> prontuarios) {
        this.prontuarios = prontuarios;
    }

    // Método para listar consultas
    public List<Prontuario> listarConsultas() {
        return prontuarios;
    }

    public String getCartaoSus() {
        return cartaoSus;
    }

    public void setCartaoSus(String cartaoSus) {
        this.cartaoSus = cartaoSus;
    }

}
