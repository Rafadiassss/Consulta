package com.example.consulta.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataPagamento;
    private BigDecimal valorPago;
    private String formaPagamento;
    private String status;


    // Construtores
    public Pagamento() {
    }

    public Pagamento(LocalDate dataPagamento, BigDecimal valorPago, String formaPagamento, String status) {
        this.dataPagamento = dataPagamento;
        this.valorPago = valorPago;
        this.formaPagamento = formaPagamento;
        this.status = status;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Método para confirmar pagamento
    public void confirmarPagamento() {
        this.status = "Confirmado";
    }
}