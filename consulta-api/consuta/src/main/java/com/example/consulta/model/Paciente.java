package com.example.consulta.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Paciente extends Usuario {

    @Column(name ="cpf")
    private String cpf;
    @Column(name = "cartao_sus")
    private String cartaoSus;
  
    public Paciente() {}
 
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

    public String getCartaoSus() {
        return cartaoSus;
    }

    public void setCartaoSus(String cartaoSus) {
        this.cartaoSus = cartaoSus;
    }

}
