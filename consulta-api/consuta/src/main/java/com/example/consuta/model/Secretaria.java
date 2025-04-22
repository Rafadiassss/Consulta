package com.example.consuta.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Secretaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String usuario;
    private String senha;

    @OneToMany
    @JoinColumn(name = "secretaria_id") // se quiser registrar quem agendou
    private List<Consulta> consultasGerenciadas;

    // Construtores
    public Secretaria() {}

    public Secretaria(String nome, String usuario, String senha) {
        this.nome = nome;
        this.usuario = usuario;
        this.senha = senha;
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<Consulta> getConsultasGerenciadas() {
        return consultasGerenciadas;
    }

    public void setConsultasGerenciadas(List<Consulta> consultasGerenciadas) {
        this.consultasGerenciadas = consultasGerenciadas;
    }

    // Método auxiliar para listar as consultas agendadas
    public List<Consulta> listarConsultasGerenciadas() {
        return consultasGerenciadas;
    }
}
