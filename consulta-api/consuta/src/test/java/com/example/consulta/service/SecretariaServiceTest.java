package com.example.consulta.service;

import com.example.consulta.dto.SecretariaRequestDTO;
import com.example.consulta.model.Secretaria;
import com.example.consulta.repository.SecretariaRepository;
import com.example.consulta.vo.SecretariaVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Secretarias")
class SecretariaServiceTest {

    @Mock
    private SecretariaRepository secretariaRepository;

    @InjectMocks
    private SecretariaService secretariaService;

    private Secretaria secretaria;
    private SecretariaRequestDTO secretariaRequestDTO;

    @BeforeEach
    void setUp() {
        secretaria = new Secretaria();
        secretaria.setNome("Ana Silva");
        ReflectionTestUtils.setField(secretaria, "id", 1L);

        secretariaRequestDTO = new SecretariaRequestDTO("Ana Silva", "111.222.333-44", "11999998888", "ana@email.com",
                "ana.silva", "senha123");
    }

    @Test
    @DisplayName("Deve listar todas as secretarias")
    void listarTodas() {
        when(secretariaRepository.findAll()).thenReturn(Collections.singletonList(secretaria));
        List<SecretariaVO> resultado = secretariaService.listarTodas();
        assertThat(resultado).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar uma secretaria por ID existente")
    void buscarPorId_quandoEncontrado() {
        when(secretariaRepository.findById(1L)).thenReturn(Optional.of(secretaria));
        Optional<SecretariaVO> resultado = secretariaService.buscarPorId(1L);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().nome()).isEqualTo("Ana Silva");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() {
        when(secretariaRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<SecretariaVO> resultado = secretariaService.buscarPorId(99L);
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar uma secretaria com sucesso")
    void salvar() {
        when(secretariaRepository.save(any(Secretaria.class))).thenReturn(secretaria);
        SecretariaVO resultado = secretariaService.salvar(secretariaRequestDTO);
        assertThat(resultado).isNotNull();
        assertThat(resultado.nome()).isEqualTo("Ana Silva");
    }

    @Test
    @DisplayName("Deve atualizar uma secretaria com sucesso")
    void atualizar_quandoEncontrado() {
        when(secretariaRepository.findById(1L)).thenReturn(Optional.of(secretaria));
        when(secretariaRepository.save(any(Secretaria.class))).thenReturn(secretaria);
        Optional<SecretariaVO> resultado = secretariaService.atualizar(1L, secretariaRequestDTO);
        assertThat(resultado).isPresent();
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao tentar atualizar secretaria inexistente")
    void atualizar_quandoNaoEncontrado() {
        when(secretariaRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<SecretariaVO> resultado = secretariaService.atualizar(99L, secretariaRequestDTO);
        assertThat(resultado).isEmpty();
        verify(secretariaRepository, never()).save(any(Secretaria.class));
    }

    @Test
    @DisplayName("Deve deletar uma secretaria existente e retornar true")
    void deletar_quandoEncontrado() {
        when(secretariaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(secretariaRepository).deleteById(1L);
        boolean resultado = secretariaService.deletar(1L);
        assertThat(resultado).isTrue();
        verify(secretariaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar secretaria inexistente")
    void deletar_quandoNaoEncontrado() {
        when(secretariaRepository.existsById(99L)).thenReturn(false);
        boolean resultado = secretariaService.deletar(99L);
        assertThat(resultado).isFalse();
        verify(secretariaRepository, never()).deleteById(anyLong());
    }
}