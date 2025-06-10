package com.example.consulta.service;

import com.example.consulta.dto.MedicoRequestDTO;
import com.example.consulta.model.Medico;
import com.example.consulta.repository.MedicoRepository;
import com.example.consulta.vo.MedicoVO;
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
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Cacheable("medicos")
    public List<MedicoVO> listarTodos() {
        return medicoRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Cacheable(value = "medico", key = "#id")
    public Optional<MedicoVO> buscarPorId(Long id) {
        return medicoRepository.findById(id).map(this::toVO);
    }

    @Caching(evict = { @CacheEvict(value = "medicos", allEntries = true) }, put = {
            @CachePut(value = "medico", key = "#result.id()") })
    public MedicoVO salvar(MedicoRequestDTO dto) {
        Medico medico = toEntity(dto);
        Medico medicoSalvo = medicoRepository.save(medico);
        return toVO(medicoSalvo);
    }

    @Caching(evict = { @CacheEvict(value = "medicos", allEntries = true) }, put = {
            @CachePut(value = "medico", key = "#id") })
    public Optional<MedicoVO> atualizar(Long id, MedicoRequestDTO dto) {
        return medicoRepository.findById(id)
                .map(medicoExistente -> {
                    medicoExistente.setNome(dto.nome());
                    medicoExistente.setUsername(dto.username());
                    medicoExistente.setEmail(dto.email());
                    medicoExistente.setTelefone(dto.telefone());
                    medicoExistente.setCrm(dto.crm());
                    medicoExistente.setEspecialidade(dto.especialidade());
                    Medico medicoAtualizado = medicoRepository.save(medicoExistente);
                    return toVO(medicoAtualizado);
                });
    }

    @Caching(evict = {
            @CacheEvict(value = "medicos", allEntries = true),
            @CacheEvict(value = "medico", key = "#id")
    })
    public boolean deletar(Long id) {
        if (medicoRepository.existsById(id)) {
            medicoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    private MedicoVO toVO(Medico medico) {
        return new MedicoVO(
                medico.getId(),
                medico.getNome(),
                medico.getUsername(),
                medico.getEmail(),
                medico.getTelefone(),
                medico.getCrm(),
                medico.getEspecialidade());
    }

    private Medico toEntity(MedicoRequestDTO dto) {
        Medico medico = new Medico();
        medico.setNome(dto.nome());
        medico.setUsername(dto.username());
        medico.setSenha(dto.senha()); // Lembre-se de codificar a senha em um cenário real!
        medico.setEmail(dto.email());
        medico.setTelefone(dto.telefone());
        medico.setCrm(dto.crm());
        medico.setEspecialidade(dto.especialidade());
        medico.setTipo("MEDICO"); // Define o tipo
        return medico;
    }
}