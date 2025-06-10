package com.example.consulta.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("MEDICO")
public class Medico extends Usuario {

    private String crm;
    private String especialidade;

    // Getters e Setters

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

}