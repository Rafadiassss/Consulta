package com.example.consulta.service;

import com.example.consulta.dto.EntradaConsultaRequestDTO;
import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.enums.TipoUsuario;
import com.example.consulta.model.EntradaConsulta;
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Usuario;
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
    private ConsultaRepository ConsultaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public ConsultaVO criarConsulta(Long idUsuario, ConsultaRequestDTO dto) {
        // Busca o usuário. Se não encontrar, lança uma exceção padrão.
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário (médico) não encontrado."));

        // Valida a permissão. Se não for médico lança IllegalArgumentException.
        if (usuario.getTipo() != TipoUsuario.MEDICO) {
            throw new IllegalArgumentException("Apenas médicos podem criar Consulta.");
        }

        Consulta consulta = new Consulta();
        consulta.setNumero(dto.numero());
        Consulta Consultasalvo = ConsultaRepository.save(consulta);

        return toVO(Consultasalvo);
    }

    public ConsultaVO buscarConsultaVO(Long idUsuario, Long idcConsulta) {
        // Valida o usuário.
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        if (usuario.getTipo() != TipoUsuario.MEDICO) {
            throw new IllegalArgumentException("Apenas médicos podem visualizar Consulta.");
        }

        // Busca o prontuário.
        Consulta consulta = ConsultaRepository.findById(idcConsulta)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrado."));

        return toVO(consulta);
    }

    public ConsultaVO adicionarEntrada(Long idConsulta, EntradaConsultaRequestDTO dto) {
        Consulta consulta = ConsultaRepository.findById(idConsulta)
                .orElseThrow(() -> new RuntimeException("Consulta principal não encontrado."));

        EntradaConsulta novaEntrada = toEntradaEntity(dto);
        novaEntrada.setDataEntrada(LocalDateTime.now());

        consulta.adicionarEntrada(novaEntrada);

        Consulta consultaAtualizado = ConsultaRepository.save(consulta);

        return toVO(consultaAtualizado);
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    private ConsultaVO toVO(Consulta consulta) {
        List<EntradaConsultaVO> entradasVO = consulta.getEntradas().stream()
                .map(this::toEntradaVO).collect(Collectors.toList());
        return new ConsultaVO(consulta.getId(), consulta.getNumero(), entradasVO);
    }

    private EntradaConsultaVO toEntradaVO(EntradaConsulta entrada) {
        return new EntradaConsultaVO(entrada.getId(), entrada.getDataEntrada(), entrada.getDiagnostico(),
                entrada.getTratamento(), entrada.getObservacoes());
    }

    private EntradaConsulta toEntradaEntity(EntradaConsultaRequestDTO dto) {
        EntradaConsulta entrada = new EntradaConsulta();
        entrada.setDiagnostico(dto.diagnostico());
        entrada.setTratamento(dto.tratamento());
        entrada.setObservacoes(dto.observacoes());
        return entrada;
    }

    @Cacheable("Consulta") // Ainda é uma boa ideia manter o cache aqui
    public List<ConsultaVO> listarTodosSemValidacao() {
        System.out.println("Buscando TODOS os Consulta no banco (requisição de desenvolvimento)...");
        return ConsultaRepository.findAll()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }
}