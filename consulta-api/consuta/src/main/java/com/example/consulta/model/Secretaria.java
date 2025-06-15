package com.example.consulta.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Secretaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private String usuario;
    private String senha;

    @OneToMany
    @JoinColumn(name = "secretaria_id")
    private List<Prontuario> prontuariosGerenciadas;

    // Construtores
    public Secretaria() {
    }

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

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Prontuario> getProntuariosGerenciadas() {
        return prontuariosGerenciadas;
    }

    public void setProntuariosGerenciadas(List<Prontuario> prontuariosGerenciadas) {
        this.prontuariosGerenciadas = prontuariosGerenciadas;
    }

    // Método auxiliar para listar as consultas agendadas
    public List<Prontuario> listarProntuariosGerenciadas() {
        return prontuariosGerenciadas;
    }
}