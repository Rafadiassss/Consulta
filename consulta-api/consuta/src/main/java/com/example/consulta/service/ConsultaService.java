package com.example.consulta.service;

import com.example.consulta.dto.EntradaConsultaRequestDTO;
import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.enums.TipoUsuario;
import com.example.consulta.model.EntradaConsulta;
import com.example.consulta.model.Agenda;
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Usuario;
import com.example.consulta.repository.AgendaRepository;
import com.example.consulta.repository.ConsultaRepository;
import com.example.consulta.repository.UsuarioRepository;
import com.example.consulta.vo.EntradaConsultaVO;
import com.example.consulta.vo.ConsultaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    public ConsultaVO criarConsulta(Long idUsuario, ConsultaRequestDTO dto) {
        // Busca o usuário. Se não encontrar, lança uma exceção padrão.
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário (médico) não encontrado."));

        // Valida a permissão. Se não for médico lança IllegalArgumentException.
        if (usuario.getTipo() != TipoUsuario.MEDICO) {
            throw new IllegalArgumentException("Apenas médicos podem criar Consulta.");
        }

        // Busca a agenda informada
        Agenda agenda = agendaRepository.findById(dto.agendaId())
                .orElseThrow(() -> new RuntimeException("Agenda não encontrada."));

        // Cria nova consulta e associa agenda
        Consulta consulta = new Consulta();
        consulta.setNumero(dto.numero());
        consulta.setAgenda(agenda);

        Consulta consultaSalvo = consultaRepository.save(consulta);

        return toVO(consultaSalvo);
    }

    public ConsultaVO buscarConsultaVO(Long idUsuario, Long idConsulta) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (usuario.getTipo() != TipoUsuario.MEDICO) {
            throw new IllegalArgumentException("Apenas médicos podem visualizar Consulta.");
        }

        Consulta consulta = consultaRepository.findById(idConsulta)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada."));

        return toVO(consulta);
    }

    public ConsultaVO adicionarEntrada(Long idConsulta, EntradaConsultaRequestDTO dto) {
        Consulta consulta = consultaRepository.findById(idConsulta)
                .orElseThrow(() -> new RuntimeException("Consulta principal não encontrada."));

        EntradaConsulta novaEntrada = toEntradaEntity(dto);
        novaEntrada.setDataEntrada(LocalDateTime.now());

        consulta.adicionarEntrada(novaEntrada);

        Consulta consultaAtualizada = consultaRepository.save(consulta);

        return toVO(consultaAtualizada);
    }

    @Cacheable("Consulta")
    public List<ConsultaVO> listarTodosSemValidacao() {
        System.out.println("Buscando TODOS os Consulta no banco (requisição de desenvolvimento)...");
        return consultaRepository.findAll()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    private ConsultaVO toVO(Consulta consulta) {
        List<EntradaConsultaVO> entradasVO = consulta.getEntradas().stream()
                .map(this::toEntradaVO)
                .collect(Collectors.toList());

        Long agendaId = consulta.getAgenda() != null ? consulta.getAgenda().getId() : null;

        return new ConsultaVO(
                consulta.getId(),
                consulta.getNumero(),
                agendaId,
                entradasVO
        );
    }

    private EntradaConsultaVO toEntradaVO(EntradaConsulta entrada) {
        return new EntradaConsultaVO(
                entrada.getId(),
                entrada.getDataEntrada(),
                entrada.getDiagnostico(),
                entrada.getTratamento(),
                entrada.getObservacoes()
        );
    }

    private EntradaConsulta toEntradaEntity(EntradaConsultaRequestDTO dto) {
        EntradaConsulta entrada = new EntradaConsulta();
        entrada.setDiagnostico(dto.diagnostico());
        entrada.setTratamento(dto.tratamento());
        entrada.setObservacoes(dto.observacoes());
        return entrada;
    }
}
