package com.example.consulta.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Paciente extends Usuario {

    private String cpf;

    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas;

    // Construtores
    public Paciente() {
    }

    public Paciente(String nome, String cpf, LocalDate dataNascimento, String usuario, String senha) {

        this.cpf = cpf;
    }

    // Getters e Setters

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
    }

    // Método para listar consultas
    public List<Consulta> listarConsultas() {
        return consultas;
    }
}