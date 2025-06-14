package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Pagamento;
import com.example.consulta.model.Prontuario;
import com.example.consulta.repository.ConsultaRepository;
import com.example.consulta.repository.MedicoRepository;
import com.example.consulta.repository.PacienteRepository;
import com.example.consulta.repository.PagamentoRepository;
import com.example.consulta.repository.ProntuarioRepository;
import com.example.consulta.vo.ConsultaVO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    // Lista todas as consultas com cache
    @Cacheable(value = "consultas")
    public List<ConsultaVO> listarTodas() {
        return consultaRepository.findAll()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    // Busca consulta por id com cache
    @Cacheable(value = "consulta", key = "#id")
    public Optional<ConsultaVO> buscarPorId(Long id) {
        return consultaRepository.findById(id).map(this::toVO);
    }

    // Deleta consulta e limpa caches relacionados
    @CacheEvict(value = { "consultas", "consulta", "prontuario" }, allEntries = true)
    @Transactional
    public boolean deletar(Long id) {
        if (consultaRepository.existsById(id)) {
            consultaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Salva nova consulta com validação e relacionamento correto
    @CacheEvict(value = "consultas", allEntries = true)
    @Transactional
    public ConsultaVO salvar(ConsultaRequestDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Medico medico = medicoRepository.findById(dto.medicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        Consulta consulta = toEntity(dto, paciente, medico);

        Consulta consultaSalva = consultaRepository.save(consulta);
        return toVO(consultaSalva);
    }

    // Atualiza consulta, incluindo pagamento e prontuario, e atualiza cache
    @CacheEvict(value = "consultas", allEntries = true)
    @CachePut(value = "consulta", key = "#id")
    @Transactional
    public Optional<ConsultaVO> atualizar(Long id, ConsultaRequestDTO dto) {
        return consultaRepository.findById(id)
                .map(consultaExistente -> {
                    Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
                    Medico medico = medicoRepository.findById(dto.medicoId())
                            .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

                    consultaExistente.setData(dto.data());
                    consultaExistente.setStatus(dto.status());
                    consultaExistente.setNomeConsulta(dto.nomeConsulta());
                    consultaExistente.setPaciente(paciente);
                    consultaExistente.setMedico(medico);

                    if (dto.pagamentoId() != null) {
                        Pagamento pagamento = pagamentoRepository.findById(dto.pagamentoId())
                                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
                        consultaExistente.setPagamento(pagamento);
                    } else {
                        consultaExistente.setPagamento(null);
                    }

                    if (dto.prontuarioId() != null) {
                        Prontuario prontuario = prontuarioRepository.findById(dto.prontuarioId())
                                .orElseThrow(() -> new RuntimeException("Prontuário não encontrado"));
                        consultaExistente.setProntuario(prontuario);
                    } else {
                        consultaExistente.setProntuario(null);
                    }

                    Consulta consultaAtualizada = consultaRepository.save(consultaExistente);
                    return toVO(consultaAtualizada);
                });
    }

    // Converte Consulta para ConsultaVO
    private ConsultaVO toVO(Consulta consulta) {
        return new ConsultaVO(
                consulta.getId(),
                consulta.getData(),
                consulta.getStatus(),
                consulta.getNomeConsulta(),
                consulta.getPaciente(),
                consulta.getMedico(),
                consulta.getPagamento(),
                consulta.getProntuario());
    }

    // Converte ConsultaRequestDTO para Consulta, relacionando entidades
    private Consulta toEntity(ConsultaRequestDTO dto, Paciente paciente, Medico medico) {
        Consulta consulta = new Consulta();
        consulta.setData(dto.data());
        consulta.setStatus(dto.status());
        consulta.setNomeConsulta(dto.nomeConsulta());
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);

        if (dto.pagamentoId() != null) {
            Pagamento pagamento = pagamentoRepository.findById(dto.pagamentoId())
                    .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
            consulta.setPagamento(pagamento);
        }

        if (dto.prontuarioId() != null) {
            Prontuario prontuario = prontuarioRepository.findById(dto.prontuarioId())
                    .orElseThrow(() -> new RuntimeException("Prontuário não encontrado"));
            consulta.setProntuario(prontuario);
        }

        return consulta;
    }
}
