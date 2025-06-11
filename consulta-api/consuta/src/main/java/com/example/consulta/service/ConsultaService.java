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
import com.example.consulta.repository.ConsultaRepository;
import com.example.consulta.repository.MedicoRepository;
import com.example.consulta.repository.PacienteRepository;
import com.example.consulta.repository.PagamentoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.consulta.model.Prontuario;
import com.example.consulta.repository.ProntuarioRepository;
import com.example.consulta.vo.ConsultaVO;

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

    @Cacheable(value = "consultas") // Cacheia a lista completa de consultas
    public List<ConsultaVO> listarTodas() {
        return consultaRepository.findAll()
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    @Cacheable(value = "consulta", key = "#id") // Cacheia uma consulta individual pelo ID
    public Optional<ConsultaVO> buscarPorId(Long id) {
        return consultaRepository.findById(id).map(this::toVO);
    }

    @CacheEvict(value = { "consultas", "consulta", "prontuario" }, allEntries = true)
    @Transactional
    public void deletar(Long id) {
        consultaRepository.deleteById(id);
    }

    @CacheEvict(value = "consultas", allEntries = true) // Limpa o cache "consultas" na criação/atualização
    @CachePut(value = "consulta", key = "#consulta.id") // Atualiza/adiciona a consulta no cache "consulta"
    @Transactional
    public ConsultaVO salvar(ConsultaRequestDTO dto) {
        // Busca as entidades relacionadas pelos IDs fornecidos no DTO.
        Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        Medico medico = medicoRepository.findById(dto.medicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        // Converte o DTO para a entidade Consulta.
        Consulta consulta = toEntity(dto, paciente, medico);

        // Salva a nova consulta.
        Consulta consultaSalva = consultaRepository.save(consulta);

        // Converte a entidade salva para VO e retorna.
        return toVO(consultaSalva);
    }

    @CacheEvict(value = "consultas", allEntries = true) // Limpa o cache de todas as consultas
    @CachePut(value = "consulta", key = "#id") // Atualiza o cache de uma consulta específica
    @Transactional
    public Optional<ConsultaVO> atualizar(Long id, ConsultaRequestDTO dto) {
        return consultaRepository.findById(id)
                .map(consultaExistente -> {
                    // Busca as novas entidades relacionadas, se os IDs foram alterados.
                    Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
                    Medico medico = medicoRepository.findById(dto.medicoId())
                            .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

                    // Atualiza os campos da consulta existente.
                    consultaExistente.setData(dto.data());
                    consultaExistente.setStatus(dto.status());
                    consultaExistente.setNomeConsulta(dto.nomeConsulta());
                    consultaExistente.setPaciente(paciente);
                    consultaExistente.setMedico(medico);

                    // Salva e converte para VO.
                    Consulta consultaAtualizada = consultaRepository.save(consultaExistente);
                    return toVO(consultaAtualizada);
                });
    }

    // --- MÉTODOS DE MAPEAMENTO ---

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

    private Consulta toEntity(ConsultaRequestDTO dto, Paciente paciente, Medico medico) {
        Consulta consulta = new Consulta();
        consulta.setData(dto.data());
        consulta.setStatus(dto.status());
        consulta.setNomeConsulta(dto.nomeConsulta());
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        // Lógica para buscar Pagamento e Prontuario se seus IDs forem fornecidos no DTO
        if (dto.pagamentoId() != null) {
            Pagamento p = pagamentoRepository.findById(dto.pagamentoId()).orElse(null);
            consulta.setPagamento(p);
        }
        if (dto.prontuarioId() != null) {
            Prontuario pr = prontuarioRepository.findById(dto.prontuarioId()).orElse(null);
            consulta.setProntuario(pr);
        }
        return consulta;
    }
}
