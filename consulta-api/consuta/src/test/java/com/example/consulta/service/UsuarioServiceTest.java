package com.example.consulta.service;

import com.example.consulta.dto.MedicoRequestDTO;
import com.example.consulta.model.Especialidade;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.repository.EspecialidadeRepository;
import com.example.consulta.repository.UsuarioRepository;
import com.example.consulta.vo.UsuarioVO;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Usuários")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EspecialidadeRepository especialidadeRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Medico medico;
    private Paciente paciente;
    private Especialidade especialidade;

    @BeforeEach
    void setUp() {
        especialidade = new Especialidade();
        especialidade.setNome("Cardiologia");
        ReflectionTestUtils.setField(especialidade, "id", 1L);

        medico = new Medico();
        medico.setNome("Dra. Ana");
        medico.setCrm("12345-SP");
        medico.setEspecialidade(especialidade);
        ReflectionTestUtils.setField(medico, "id", 1L);

        paciente = new Paciente();
        paciente.setNome("Carlos Souza");
        paciente.setCpf("111.222.333-44");
        ReflectionTestUtils.setField(paciente, "id", 2L);
    }

    @Test
    @DisplayName("Deve converter um Medico para UsuarioVO corretamente")
    void toVO_quandoUsuarioEhMedico() {
        // Simula o repositório encontrando a entidade Medico.
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(medico));

        // Chama o método de busca do serviço.
        Optional<UsuarioVO> resultado = usuarioService.buscarPorId(1L);

        // Verifica se o VO foi criado com os campos específicos de Medico.
        assertThat(resultado).isPresent();
        assertThat(resultado.get().crm()).isEqualTo("12345-SP");
        assertThat(resultado.get().especialidade()).isEqualTo("Cardiologia");
        assertThat(resultado.get().cpf()).isNull();
    }

    @Test
    @DisplayName("Deve converter um Paciente para UsuarioVO corretamente")
    void toVO_quandoUsuarioEhPaciente() {
        // Simula o repositório encontrando a entidade Paciente.
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(paciente));

        // Chama o método de busca do serviço.
        Optional<UsuarioVO> resultado = usuarioService.buscarPorId(2L);

        // Verifica se o VO foi criado com os campos específicos de Paciente.
        assertThat(resultado).isPresent();
        assertThat(resultado.get().cpf()).isEqualTo("111.222.333-44");
        assertThat(resultado.get().crm()).isNull();
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar Medico se a Especialidade não existir")
    void salvarMedico_quandoEspecialidadeNaoExiste() {
        // Prepara um DTO que aponta para uma especialidade inexistente.
        MedicoRequestDTO medicoRequestDTO = new MedicoRequestDTO("Nome", "user", "senha", "e@mail.com", null, "123",
                99L);

        // Simula o repositório NÃO encontrando a especialidade.
        when(especialidadeRepository.findById(99L)).thenReturn(Optional.empty());

        // Verifica se a chamada ao serviço lança a exceção esperada.
        assertThrows(RuntimeException.class, () -> {
            usuarioService.salvarMedico(medicoRequestDTO);
        });
    }
}