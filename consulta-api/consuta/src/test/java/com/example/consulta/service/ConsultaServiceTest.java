package com.example.consulta.service;

import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.enums.TipoUsuario;
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Usuario;
import com.example.consulta.repository.ConsultaRepository;
import com.example.consulta.repository.UsuarioRepository;
import com.example.consulta.vo.ConsultaVO;
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
@DisplayName("Testes da Camada de Serviço de Prontuários")
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ConsultaService consultaService;

    private Usuario medico;
    private Usuario paciente;
    private Consulta prontuario;
    private ConsultaRequestDTO consultaRequestDTO;

    @BeforeEach
    void setUp() {
        medico = new Usuario();
        medico.setTipo(TipoUsuario.MEDICO);
        ReflectionTestUtils.setField(medico, "id", 1L);

        paciente = new Usuario();
        paciente.setTipo(TipoUsuario.MEDICO);
        ReflectionTestUtils.setField(paciente, "id", 2L);

        prontuario = new Consulta();
        prontuario.setNumero("PRT-001");
        ReflectionTestUtils.setField(prontuario, "id", 10L);

        consultaRequestDTO = new ConsultaRequestDTO("PRT-002");
    }

    @Test
    @DisplayName("Deve criar um prontuário com sucesso quando o usuário é um médico")
    void criarProntuario_comSucesso() {
        // Simula o repositório encontrando o usuário médico.
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(medico));
        // Simula a ação de salvar o prontuário.
        when(consultaRepository.save(any(Consulta.class))).thenReturn(prontuario);

        // Chama o método do serviço.
        ConsultaVO resultado = consultaService.criarConsulta(1L, consultaRequestDTO);

        // Verifica o resultado.
        assertThat(resultado).isNotNull();
        assertThat(resultado.numero()).isEqualTo("PRT-001");
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar prontuário se o usuário não for médico")
    void criarProntuario_quandoUsuarioNaoEhMedico() {
        // Simula o repositório encontrando um usuário que é paciente.
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(paciente));

        // Verifica se a chamada lança a exceção de argumento ilegal.
        assertThrows(IllegalArgumentException.class, () -> {
            consultaService.criarConsulta(2L, consultaRequestDTO);
        });

        // Garante que o prontuário nunca foi salvo.
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve buscar um prontuário com sucesso")
    void buscarProntuario_comSucesso() {
        // Simula a busca bem-sucedida do médico e do prontuário.
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(consultaRepository.findById(10L)).thenReturn(Optional.of(prontuario));

        // Chama o método de serviço.
        ConsultaVO resultado = consultaService.buscarConsultaVO(1L, 10L);

        // Verifica o resultado.
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar prontuário se o prontuário não for encontrado")
    void buscarProntuario_quandoNaoEncontrado() {
        // Simula a busca do médico bem-sucedida.
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(medico));
        // Simula a falha na busca do prontuário.
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());

        // Verifica se a exceção correta é lançada.
        assertThrows(RuntimeException.class, () -> {
            consultaService.buscarConsultaVO(1L, 99L);
        });
    }
}