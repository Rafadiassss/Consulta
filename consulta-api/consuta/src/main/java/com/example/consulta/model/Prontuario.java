// package com.example.consulta.model;

// import jakarta.persistence.*;
// import com.fasterxml.jackson.annotation.JsonIgnore;

// @Entity
// public class Prontuario {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     private String numero;
//     private String diagnostico;
//     private String tratamento;
//     private String observacoes;

//     @OneToOne(mappedBy = "prontuario")
//     @JsonIgnore
//     private Consulta consulta; // Relacionamento bidirecional

//     // Construtor padrão
//     public Prontuario() {
//     }

//     // Construtor completo
//     public Prontuario(String numero, String diagnostico, String tratamento, String observacoes) {
//         this.numero = numero;
//         this.diagnostico = diagnostico;
//         this.tratamento = tratamento;
//         this.observacoes = observacoes;
//     }

//     // Getters e Setters
//     public Long getId() {
//         return id;
//     }

//     public void setId(Long id) {
//         this.id = id;
//     }

//     public String getNumero() {
//         return numero;
//     }

//     public void setNumero(String numero) {
//         this.numero = numero;
//     }

//     public String getDiagnostico() {
//         return diagnostico;
//     }

//     public void setDiagnostico(String diagnostico) {
//         this.diagnostico = diagnostico;
//     }

//     public String getTratamento() {
//         return tratamento;
//     }

//     public void setTratamento(String tratamento) {
//         this.tratamento = tratamento;
//     }

//     public String getObservacoes() {
//         return observacoes;
//     }

//     public void setObservacoes(String observacoes) {
//         this.observacoes = observacoes;
//     }

//     public Consulta getConsulta() {
//         return consulta;
//     }

//     public void setConsulta(Consulta consulta) {
//         this.consulta = consulta;
//     }
// }

package com.example.consulta.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero; // O número do prontuário pode ser único e imutável

    // Os campos antigos (diagnostico, tratamento, observacoes) são REMOVIDOS
    // E substituídos por uma lista de entradas
    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntradaProntuario> entradas = new ArrayList<>();

    // ... Getters e Setters para id, numero e entradas ...

    // Método auxiliar para adicionar uma nova entrada de forma segura
    public void adicionarEntrada(EntradaProntuario entrada) {
        entradas.add(entrada);
        entrada.setProntuario(this);
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

    public List<EntradaProntuario> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<EntradaProntuario> entradas) {
        this.entradas = entradas;
    }
}
