package com.example.consulta.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
  
    private Long id; 
 
    private String numero;
    private String diagnostico;
    private String tratamento;
    private String observacoes;
  
    // E substituídos por uma lista de entradas
    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntradaProntuario> entradas = new ArrayList<>();
  
    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Usuario medico;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Usuario paciente;
    // Construtor padrão
    public Prontuario() {}

    // Método auxiliar para adicionar uma nova entrada de forma segura
    public void adicionarEntrada(EntradaProntuario entrada) {
        entradas.add(entrada);
        entrada.setProntuario(this);
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

    public List<EntradaProntuario> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<EntradaProntuario> entradas) {
        this.entradas = entradas;
    }
}