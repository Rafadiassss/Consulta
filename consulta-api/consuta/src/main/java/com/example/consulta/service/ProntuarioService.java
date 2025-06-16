package com.example.consulta.service;

import com.example.consulta.dto.ProntuarioRequestDTO;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Pagamento;
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Prontuario;
import com.example.consulta.repository.MedicoRepository;
import com.example.consulta.repository.PacienteRepository;
import com.example.consulta.repository.PagamentoRepository;
import com.example.consulta.repository.ConsultaRepository;
import com.example.consulta.repository.ProntuarioRepository;
import com.example.consulta.vo.ProntuarioVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProntuarioService {

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    // Lista todos os prontuários
    @Cacheable(value = "prontuarios")
    public List<ProntuarioVO> listarTodos() {
        return prontuarioRepository.findAll()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    // Busca por ID
    @Cacheable(value = "prontuario", key = "#id")
    public Optional<ProntuarioVO> buscarPorId(Long id) {
        return prontuarioRepository.findById(id)
                .map(this::toVO);
    }

    // Criar novo prontuário
    @CacheEvict(value = "prontuarios", allEntries = true)
    @Transactional
    public ProntuarioVO salvar(ProntuarioRequestDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Medico medico = medicoRepository.findById(dto.medicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        Prontuario prontuario = new Prontuario();
        prontuario.setData(dto.data());
        prontuario.setStatus(dto.status());
        prontuario.setNomeConsulta(dto.nomeConsulta());
        prontuario.setPaciente(paciente);
        prontuario.setMedico(medico);

        if (dto.pagamentoId() != null) {
            Pagamento pagamento = pagamentoRepository.findById(dto.pagamentoId())
                    .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
            prontuario.setPagamento(pagamento);
        }

        if (dto.consultaId() != null) {
            Consulta consulta = consultaRepository.findById(dto.consultaId())
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
            prontuario.setProntuario(consulta);
        }

        Prontuario salvo = prontuarioRepository.save(prontuario);
        return toVO(salvo);
    }

    // Atualizar prontuário
    @CacheEvict(value = "prontuarios", allEntries = true)
    @CachePut(value = "prontuario", key = "#id")
    @Transactional
    public Optional<ProntuarioVO> atualizar(Long id, ProntuarioRequestDTO dto) {
        return prontuarioRepository.findById(id)
                .map(prontuarioExistente -> {
                    Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
                    Medico medico = medicoRepository.findById(dto.medicoId())
                            .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

                    prontuarioExistente.setData(dto.data());
                    prontuarioExistente.setStatus(dto.status());
                    prontuarioExistente.setNomeConsulta(dto.nomeConsulta());
                    prontuarioExistente.setPaciente(paciente);
                    prontuarioExistente.setMedico(medico);

                    if (dto.pagamentoId() != null) {
                        Pagamento pagamento = pagamentoRepository.findById(dto.pagamentoId())
                                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
                        prontuarioExistente.setPagamento(pagamento);
                    } else {
                        prontuarioExistente.setPagamento(null);
                    }

                    if (dto.consultaId() != null) {
                        Consulta consulta = consultaRepository.findById(dto.consultaId())
                                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
                        prontuarioExistente.setProntuario(consulta);
                    } else {
                        prontuarioExistente.setProntuario(null);
                    }

                    Prontuario atualizado = prontuarioRepository.save(prontuarioExistente);
                    return toVO(atualizado);
                });
    }

    // Deletar
    @CacheEvict(value = { "prontuarios", "prontuario" }, allEntries = true)
    @Transactional
    public boolean deletar(Long id) {
        if (prontuarioRepository.existsById(id)) {
            prontuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Conversor VO
    private ProntuarioVO toVO(Prontuario prontuario) {
        return new ProntuarioVO(
                prontuario.getId(),
                prontuario.getData(),
                prontuario.getStatus(),
                prontuario.getNomePontuario(),
                prontuario.getPaciente(),
                prontuario.getMedico(),
                prontuario.getPagamento(),
                prontuario.getProntuario()
        );
    }
}
