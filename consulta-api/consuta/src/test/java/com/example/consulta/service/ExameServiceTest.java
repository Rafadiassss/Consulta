package com.example.consulta.service;

import com.example.consulta.model.Exame;
import com.example.consulta.repository.ExameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Exames")
class ExameServiceTest {

    @Mock
    private ExameRepository exameRepository;

    @InjectMocks
    private ExameService exameService;

    private Exame exame;

    @BeforeEach
    void setUp() {
        exame = new Exame("Hemograma Completo", "Resultados normais", "Sem observações", null);
        // Supondo que Exame tenha um setId para testes
        // exame.setId(1L);
    }

    @Test
    @DisplayName("Deve buscar um exame por ID existente")
    void buscarPorId_quandoEncontrado() {
        when(exameRepository.findById(1L)).thenReturn(Optional.of(exame));
        Optional<Exame> resultado = exameService.buscarPorId(1L);
        assertThat(resultado).isPresent();
    }

    @Test
    @DisplayName("Deve salvar um exame")
    void salvar() {
        when(exameRepository.save(any(Exame.class))).thenReturn(exame);
        Exame resultado = exameService.salvar(new Exame());
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um exame existente e retornar true")
    void deletar_quandoExameExiste() {
        // Simula que o exame existe no banco.
        when(exameRepository.existsById(1L)).thenReturn(true);
        // Configura a chamada ao método 'deleteById'.
        doNothing().when(exameRepository).deleteById(1L);

        // Chama o método de serviço.
        boolean resultado = exameService.deletar(1L);

        // Verifica o retorno e a chamada ao repositório.
        assertThat(resultado).isTrue();
        verify(exameRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar um exame inexistente")
    void deletar_quandoExameNaoExiste() {
        // Simula que o exame NÃO existe.
        when(exameRepository.existsById(99L)).thenReturn(false);

        // Chama o método de serviço.
        boolean resultado = exameService.deletar(99L);

        // Verifica o retorno 'false'.
        assertThat(resultado).isFalse();
        // Confirma que 'deleteById' NUNCA foi chamado.
        verify(exameRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve atualizar um exame com sucesso")
    void atualizar_quandoEncontrado() {
        Exame dadosNovos = new Exame("Hemograma", "Resultados alterados", "Repetir em 15 dias", null);

        // Simula a busca da entidade existente.
        when(exameRepository.findById(1L)).thenReturn(Optional.of(exame));
        // Simula a ação de salvar.
        when(exameRepository.save(any(Exame.class))).thenReturn(dadosNovos);

        // Chama o serviço de atualização.
        Optional<Exame> resultado = exameService.atualizar(1L, dadosNovos);

        // Verifica se o exame foi atualizado.
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getObservacoes()).isEqualTo("Repetir em 15 dias");
    }
}