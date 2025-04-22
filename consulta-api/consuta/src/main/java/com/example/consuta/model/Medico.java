package com.example.consuta.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String especialidade;
    private String registroProfissional;
    private String usuario;
    private String senha;

    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultas;

    // Construtores
    public Medico() {}

    public Medico(String nome, String especialidade, String registroProfissional, String usuario, String senha) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.registroProfissional = registroProfissional;
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

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public void setRegistroProfissional(String registroProfissional) {
        this.registroProfissional = registroProfissional;
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

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
    }

    // Método para listar consultas agendadas
    public List<Consulta> listarConsultasAgendadas() {
        return consultas;
    }
}
