package com.example.consulta.service;

import com.example.consulta.dto.MedicoRequestDTO;
import com.example.consulta.model.Especialidade;
import com.example.consulta.model.Medico;
import com.example.consulta.repository.EspecialidadeRepository;
import com.example.consulta.repository.MedicoRepository;
import com.example.consulta.vo.MedicoVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Médicos")
class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private EspecialidadeRepository especialidadeRepository;

    @InjectMocks
    private MedicoService medicoService;

    private Medico medico;
    private Especialidade especialidade;
    private MedicoRequestDTO medicoRequestDTO;

    @BeforeEach
    void setUp() {
        // Prepara uma especialidade de teste.
        especialidade = new Especialidade();
        especialidade.setNome("Cardiologia");
        ReflectionTestUtils.setField(especialidade, "id", 1L);

        // Prepara um médico de teste já associado à especialidade.
        medico = new Medico();
        medico.setNome("Dra. Ana");
        medico.setCrm("12345-SP");
        medico.setEspecialidade(especialidade);
        ReflectionTestUtils.setField(medico, "id", 1L);

        // Prepara um DTO para as requisições de salvar/atualizar.
        medicoRequestDTO = new MedicoRequestDTO("Dr. House", "housemd", "senha", "house@email.com", null, "98765-SP",
                1L);
    }

    @Test
    @DisplayName("Deve salvar um médico com sucesso")
    void salvar_comSucesso() {
        // Simula o repositório encontrando a especialidade associada.
        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(especialidade));
        // Simula a ação de salvar, que retorna a entidade com ID.
        when(medicoRepository.save(any(Medico.class))).thenReturn(medico);

        // Chama o método de serviço com o DTO.
        MedicoVO resultado = medicoService.salvar(medicoRequestDTO);

        // Verifica se o VO retornado tem os dados esperados.
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nomeEspecialidade()).isEqualTo("Cardiologia");
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar se a especialidade não existir")
    void salvar_quandoEspecialidadeNaoExiste() {
        // Simula o repositório NÃO encontrando a especialidade.
        when(especialidadeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Verifica se a chamada ao serviço lança a exceção esperada.
        assertThrows(RuntimeException.class, () -> {
            medicoService.salvar(medicoRequestDTO);
        });

        // Garante que, com o erro, o médico nunca foi salvo.
        verify(medicoRepository, never()).save(any(Medico.class));
    }
}