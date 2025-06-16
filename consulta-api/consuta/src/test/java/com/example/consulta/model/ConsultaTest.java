package com.example.consulta.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes da Entidade Prontuario")
class ConsultaTest {

    @Test
    @DisplayName("Deve adicionar uma nova entrada à lista de histórico corretamente")
    void adicionarEntrada_deveIncrementarListaEConfigurarRelacao() {
        // Cria uma instância do prontuário, que começa com uma lista de entradas vazia.
        Consulta prontuario = new Consulta();
        assertThat(prontuario.getEntradas()).isNotNull().isEmpty();

        // Cria a nova entrada que será adicionada.
        EntradaConsulta novaEntrada = new EntradaConsulta();
        novaEntrada.setDiagnostico("Febre e dor de cabeça");

        // Chama o método
        prontuario.adicionarEntrada(novaEntrada);

        // Verifica os resultados.
        assertThat(prontuario.getEntradas()).hasSize(1).contains(novaEntrada);
        // Verifica a relação bidirecional: a 'novaEntrada' agora "sabe" a qual
        // prontuário ela pertence.
        assertThat(novaEntrada.getConsulta()).isEqualTo(prontuario);
    }
}