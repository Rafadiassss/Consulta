package com.example.consulta.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataAgendada;

    @ElementCollection
    private List<LocalTime> horarios = new ArrayList<>();

    // Construtores
    public Agenda() {
    }

    // Adicione este construtor na sua classe Agenda.java
    public Agenda(Long id, LocalDate dataAgendada, List<LocalTime> horarios) {
        this.id = id;
        this.dataAgendada = dataAgendada;
        this.horarios = horarios;
    }

    public Agenda(LocalDate dataAgendada) {
        this.dataAgendada = dataAgendada;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataAgendada() {
        return dataAgendada;
    }

    public void setDataAgendada(LocalDate dataAgendada) {
        this.dataAgendada = dataAgendada;
    }

    public List<LocalTime> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<LocalTime> horarios) {
        this.horarios = horarios;
    }

    // Métodos auxiliares
    public void adicionarHorario(LocalTime horario) {
        if (!horarios.contains(horario)) {
            horarios.add(horario);
        }
    }

    public void removerHorario(LocalTime horario) {
        horarios.remove(horario);
    }

    public List<LocalTime> listarHorariosDisponiveis() {
        return new ArrayList<>(horarios);
    }
}