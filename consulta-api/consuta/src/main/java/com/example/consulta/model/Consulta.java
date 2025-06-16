
package com.example.consulta.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "agenda_id")
    @JsonIgnoreProperties("agenda")
    private Agenda agenda;

    private String numero;

    // E substituídos por uma lista de entradas
    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntradaConsulta> entradas = new ArrayList<>();

    // Método auxiliar para adicionar uma nova entrada de forma segura
    public void adicionarEntrada(EntradaConsulta entrada) {
        entradas.add(entrada);
        entrada.setConsulta(this);
    }

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

    public List<EntradaConsulta> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<EntradaConsulta> entradas) {
        this.entradas = entradas;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }
    
}