package com.example.consulta.service;

import com.example.consulta.dto.ExameRequestDTO;
import com.example.consulta.model.Prontuario;
import com.example.consulta.model.Exame;
import com.example.consulta.repository.ProntuarioRepository;
import com.example.consulta.repository.ExameRepository;
import com.example.consulta.vo.ExameVO;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Exames")
class ExameServiceTest {

    @Mock
    private ExameRepository exameRepository;
    @Mock
    private ProntuarioRepository prontuarioRepository;

    @InjectMocks
    private ExameService exameService;

    private Exame exame;
    private Prontuario prontuario;
    private ExameRequestDTO exameRequestDTO;

    @BeforeEach
    void setUp() {
        // Prepara uma prontuario de base.
        prontuario = new Prontuario();
        prontuario.setId(10L);

        // Prepara um exame de base, já associado à prontuario.
        exame = new Exame("Hemograma Completo", "Resultados normais", "Sem observações", prontuario);
        // Isso simula uma entidade que foi salva e recebeu um ID do banco.
        ReflectionTestUtils.setField(exame, "id", 1L);

        // Prepara um DTO para as requisições.
        exameRequestDTO = new ExameRequestDTO("Raio-X", "Laudo pendente", "Paciente com tosse.", 10L);
    }

    @Test
    @DisplayName("Deve listar todos os exames e converter para VO")
    void listarTodos() {
        // Simula o repositório retornando uma lista com nosso exame.
        when(exameRepository.findAll()).thenReturn(Collections.singletonList(exame));

        // Chama o método do serviço.
        List<ExameVO> resultado = exameService.listarTodos();

        // Verifica se a lista foi retornada e convertida corretamente.
        assertThat(resultado).isNotNull().hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Hemograma Completo");
    }

    @Test
    @DisplayName("Deve salvar a partir de um DTO e retornar um VO")
    void salvar() {
        // Simula o repositório encontrando a prontuario associada.
        when(prontuarioRepository.findById(10L)).thenReturn(Optional.of(prontuario));
        // Simula a ação de salvar, que retorna a entidade com ID.
        when(exameRepository.save(any(Exame.class))).thenReturn(exame);

        // Chama o método de serviço com o DTO.
        ExameVO resultado = exameService.salvar(exameRequestDTO);

        // Verifica se o VO retornado tem os dados esperados.
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Hemograma Completo");
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar se a prontuario associada não existir")
    void salvar_quandoprontuarioNaoExiste() {
        // Simula o repositório NÃO encontrando a prontuario.
        when(prontuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Verifica se a chamada ao serviço lança a exceção esperada.
        assertThrows(RuntimeException.class, () -> {
            exameService.salvar(exameRequestDTO);
        });

        // Garante que, com o erro, o exame nunca foi salvo.
        verify(exameRepository, never()).save(any(Exame.class));
    }

    @Test
    @DisplayName("Deve atualizar um exame existente com sucesso")
    void atualizar_quandoEncontrado() {
        // Simula a busca do exame e da prontuario existentes.
        when(exameRepository.findById(1L)).thenReturn(Optional.of(exame));
        when(prontuarioRepository.findById(10L)).thenReturn(Optional.of(prontuario));
        // Simula a ação de salvar, retornando a entidade atualizada.
        when(exameRepository.save(any(Exame.class))).thenReturn(exame);

        // Chama o serviço de atualização.
        Optional<ExameVO> resultado = exameService.atualizar(1L, exameRequestDTO);

        // Verifica se o exame foi atualizado.
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve deletar um exame existente e retornar true")
    void deletar_quandoEncontrado() {
        // Simula que o exame existe.
        when(exameRepository.existsById(1L)).thenReturn(true);
        // Configura a chamada ao método 'deleteById'.
        doNothing().when(exameRepository).deleteById(1L);

        // Chama o método de serviço.
        boolean resultado = exameService.deletar(1L);

        // Verifica o resultado e a chamada ao repositório.
        assertThat(resultado).isTrue();
        verify(exameRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar um exame inexistente")
    void deletar_quandoNaoEncontrado() {
        // Simula que o exame NÃO existe.
        when(exameRepository.existsById(99L)).thenReturn(false);

        // Chama o método de serviço.
        boolean resultado = exameService.deletar(99L);

        // Verifica o resultado.
        assertThat(resultado).isFalse();
        // Confirma que 'deleteById' NUNCA foi chamado.
        verify(exameRepository, never()).deleteById(anyLong());
    }
}