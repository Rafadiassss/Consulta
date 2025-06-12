package com.example.consulta.service;

import com.example.consulta.dto.EspecialidadeRequestDTO;
import com.example.consulta.model.Especialidade;
import com.example.consulta.repository.EspecialidadeRepository;
import com.example.consulta.vo.EspecialidadeVO;
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
@DisplayName("Testes da Camada de Serviço de Especialidades")
class EspecialidadeServiceTest {

    @Mock
    private EspecialidadeRepository especialidadeRepository;

    @InjectMocks
    private EspecialidadeService especialidadeService;

    private Especialidade especialidade;
    private EspecialidadeRequestDTO especialidadeRequestDTO;

    @BeforeEach
    void setUp() {
        // Prepara uma entidade base que simula um retorno do repositório.
        especialidade = new Especialidade();
        especialidade.setNome("Cardiologia");
        especialidade.setDescricao("Cuida do coração.");
        ReflectionTestUtils.setField(especialidade, "id", 1L);

        // Prepara um DTO para simular uma requisição de entrada.
        especialidadeRequestDTO = new EspecialidadeRequestDTO("Cardiologia", "Cuida do coração.");
    }

    @Test
    @DisplayName("Deve listar todas as especialidades")
    void listarTodas() {
        // Simula o repositório retornando uma lista com nossa especialidade.
        when(especialidadeRepository.findAll()).thenReturn(Collections.singletonList(especialidade));

        // Chama o método do serviço.
        List<EspecialidadeVO> resultado = especialidadeService.listarTodas();

        // Verifica se a lista de VOs foi retornada corretamente.
        assertThat(resultado).isNotNull().hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Cardiologia");
    }

    @Test
    @DisplayName("Deve buscar por ID existente e retornar um Optional de VO")
    void buscarPorId_quandoEncontrado() {
        // Simula o repositório encontrando a entidade.
        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(especialidade));

        // Chama o método do serviço.
        Optional<EspecialidadeVO> resultado = especialidadeService.buscarPorId(1L);

        // Verifica se o VO correto foi retornado dentro do Optional.
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve salvar a partir de um DTO e retornar um VO")
    void salvar() {
        // Simula a ação de salvar do repositório.
        when(especialidadeRepository.save(any(Especialidade.class))).thenReturn(especialidade);

        // Chama o método do serviço passando o DTO.
        EspecialidadeVO resultado = especialidadeService.salvar(especialidadeRequestDTO);

        // Verifica se o VO retornado contém os dados esperados.
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve atualizar uma especialidade existente com sucesso")
    void atualizar_quandoEncontrado() {
        // Simula a busca da entidade existente.
        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(especialidade));
        // Simula a ação de salvar, retornando a entidade com os dados atualizados.
        when(especialidadeRepository.save(any(Especialidade.class))).thenReturn(especialidade);

        // Chama o serviço de atualização.
        Optional<EspecialidadeVO> resultado = especialidadeService.atualizar(1L, especialidadeRequestDTO);

        // Verifica se a operação retornou um VO presente.
        assertThat(resultado).isPresent();
        // Confirma que o método 'save' foi chamado.
        verify(especialidadeRepository).save(any(Especialidade.class));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao tentar atualizar especialidade inexistente")
    void atualizar_quandoNaoEncontrado() {
        // Simula o repositório não encontrando a especialidade.
        when(especialidadeRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o serviço.
        Optional<EspecialidadeVO> resultado = especialidadeService.atualizar(99L, especialidadeRequestDTO);

        // Verifica se o resultado é vazio e se 'save' nunca foi chamado.
        assertThat(resultado).isEmpty();
        verify(especialidadeRepository, never()).save(any(Especialidade.class));
    }

    @Test
    @DisplayName("Deve deletar uma especialidade existente e retornar true")
    void deletar_quandoEncontrado() {
        // Simula que a especialidade existe.
        when(especialidadeRepository.existsById(1L)).thenReturn(true);
        // Configura a chamada ao método 'deleteById'.
        doNothing().when(especialidadeRepository).deleteById(1L);

        // Chama o método de serviço.
        boolean resultado = especialidadeService.deletar(1L);

        // Verifica se o resultado é 'true' e se a exclusão foi chamada.
        assertThat(resultado).isTrue();
        verify(especialidadeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar especialidade inexistente")
    void deletar_quandoNaoEncontrado() {
        // Simula que a especialidade NÃO existe.
        when(especialidadeRepository.existsById(99L)).thenReturn(false);

        // Chama o método de serviço.
        boolean resultado = especialidadeService.deletar(99L);

        // Verifica se o resultado é 'false' e se 'deleteById' NUNCA foi chamado.
        assertThat(resultado).isFalse();
        verify(especialidadeRepository, never()).deleteById(anyLong());
    }
}