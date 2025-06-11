package com.example.consulta.service;

import com.example.consulta.dto.PacienteRequestDTO;
import com.example.consulta.model.Paciente;
import com.example.consulta.repository.PacienteRepository;
import com.example.consulta.vo.PacienteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Cacheable("pacientes")
    public List<PacienteVO> listarTodos() {
        return pacienteRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Cacheable(value = "paciente", key = "#id")
    public Optional<PacienteVO> buscarPorId(Long id) {
        return pacienteRepository.findById(id).map(this::toVO);
    }

    @Caching(evict = { @CacheEvict(value = "pacientes", allEntries = true) }, put = {
            @CachePut(value = "paciente", key = "#result.id()") })
    public PacienteVO salvar(PacienteRequestDTO dto) {
        Paciente paciente = toEntity(dto);
        Paciente pacienteSalvo = pacienteRepository.save(paciente);
        return toVO(pacienteSalvo);
    }

    @Caching(evict = { @CacheEvict(value = "pacientes", allEntries = true) }, put = {
            @CachePut(value = "paciente", key = "#id") })
    public Optional<PacienteVO> atualizar(Long id, PacienteRequestDTO dto) {
        return pacienteRepository.findById(id)
                .map(pacienteExistente -> {
                    // Atualiza os campos da entidade com os dados do DTO.
                    pacienteExistente.setNome(dto.nome());
                    pacienteExistente.setUsername(dto.username());
                    pacienteExistente.setEmail(dto.email());
                    pacienteExistente.setTelefone(dto.telefone());
                    pacienteExistente.setDataNascimento(dto.dataNascimento());
                    pacienteExistente.setCpf(dto.cpf());
                    // Salva a entidade atualizada.
                    Paciente pacienteAtualizado = pacienteRepository.save(pacienteExistente);
                    // Converte para VO e retorna.
                    return toVO(pacienteAtualizado);
                });
    }

    @Caching(evict = {
            @CacheEvict(value = "pacientes", allEntries = true),
            @CacheEvict(value = "paciente", key = "#id")
    })
    public boolean deletar(Long id) {
        if (pacienteRepository.existsById(id)) {
            pacienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    private PacienteVO toVO(Paciente paciente) {
        return new PacienteVO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getUsername(),
                paciente.getEmail(),
                paciente.getTelefone(),
                paciente.getDataNascimento(),
                paciente.getCpf(),
                paciente.getCartaoSus());
    }

    private Paciente toEntity(PacienteRequestDTO dto) {
        Paciente paciente = new Paciente();
        paciente.setNome(dto.nome());
        paciente.setUsername(dto.username());
        paciente.setSenha(dto.senha());
        paciente.setEmail(dto.email());
        paciente.setTelefone(dto.telefone());
        paciente.setDataNascimento(dto.dataNascimento());
        paciente.setCpf(dto.cpf());
        paciente.setCartaoSus(dto.cartaoSus());
        paciente.setTipo("PACIENTE");
        return paciente;
    }
}