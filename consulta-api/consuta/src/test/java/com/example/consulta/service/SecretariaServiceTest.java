package com.example.consulta.service;

import com.example.consulta.model.Secretaria;
import com.example.consulta.repository.SecretariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Secretarias")
class SecretariaServiceTest {

    @Mock
    private SecretariaRepository secretariaRepository;

    @InjectMocks
    private SecretariaService secretariaService;

    private Secretaria secretaria;

    @BeforeEach
    void setUp() {
        secretaria = new Secretaria();
        secretaria.setId(1L);
        secretaria.setNome("Ana Silva");
    }

    @Test
    @DisplayName("Deve listar todas as secretarias")
    void listarTodas() {
        // Configura o mock do repositório para retornar uma lista com nossa secretaria.
        when(secretariaRepository.findAll()).thenReturn(Collections.singletonList(secretaria));

        // Chama o método do serviço.
        List<Secretaria> resultado = secretariaService.listarTodas();

        // Verifica se o resultado não é nulo e tem o tamanho esperado.
        assertThat(resultado).isNotNull().hasSize(1);
        // Garante que o método 'findAll' do repositório foi chamado.
        verify(secretariaRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar uma secretaria por ID existente")
    void buscarPorId_quandoEncontrado() {
        // Configura o mock para encontrar a secretaria.
        when(secretariaRepository.findById(1L)).thenReturn(Optional.of(secretaria));

        // Chama o método do serviço.
        Optional<Secretaria> resultado = secretariaService.buscarPorId(1L);

        // Verifica se o Optional contém o objeto esperado.
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Ana Silva");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() {
        // Configura o mock para retornar um Optional vazio.
        when(secretariaRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o método do serviço.
        Optional<Secretaria> resultado = secretariaService.buscarPorId(99L);

        // Verifica se o Optional está vazio.
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar uma secretaria")
    void salvar() {
        // Configura o mock para retornar a secretaria quando 'save' for chamado.
        when(secretariaRepository.save(any(Secretaria.class))).thenReturn(secretaria);

        // Chama o método de salvar.
        Secretaria resultado = secretariaService.salvar(new Secretaria());

        // Verifica se o resultado não é nulo.
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar uma secretaria por ID")
    void deletar() {
        // Configura o mock para não fazer nada quando 'deleteById' for chamado (é um
        // método void).
        doNothing().when(secretariaRepository).deleteById(1L);

        // Chama o método do serviço.
        secretariaService.deletar(1L);

        // Garante que o método 'deleteById' do repositório foi chamado exatamente uma
        // vez com o ID correto.
        verify(secretariaRepository, times(1)).deleteById(1L);
    }
}