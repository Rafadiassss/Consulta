package com.example.consulta.service;

import com.example.consulta.dto.EspecialidadeRequestDTO;
import com.example.consulta.dto.EspecialidadeResponseDTO;
import com.example.consulta.model.Especialidade;
import com.example.consulta.repository.EspecialidadeRepository;
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
public class EspecialidadeService {

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Cacheable("especialidades") // Cache para a lista completa
    public List<EspecialidadeResponseDTO> listarTodas() {
        System.out.println("Buscando TODAS as especialidades no banco...");
        return especialidadeRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "especialidade", key = "#id") // Cache para itens individuais
    public Optional<EspecialidadeResponseDTO> buscarPorId(Long id) {
        System.out.println("Buscando especialidade de ID " + id + " no banco...");
        return especialidadeRepository.findById(id).map(this::toResponseDTO);
    }


    // O método salvar recebe o DTO de requisição.
    @Caching(
            evict = { @CacheEvict(value = "especialidades", allEntries = true) }, // Limpa o cache da lista
            put = { @CachePut(value = "especialidade", key = "#result.id()") } // Adiciona/atualiza o item salvo no cache individual
    )
    public EspecialidadeResponseDTO salvar(EspecialidadeRequestDTO dto) {
        System.out.println("Salvando nova especialidade...");
        Especialidade especialidade = toEntity(dto);
        Especialidade especialidadeSalva = especialidadeRepository.save(especialidade);
        return toResponseDTO(especialidadeSalva);
    }
    
    // O método atualizar recebe o DTO e retorna um DTO.
    @Caching(
            evict = { @CacheEvict(value = "especialidades", allEntries = true) }, // Limpa o cache da lista
            put = { @CachePut(value = "especialidade", key = "#id") } // Atualiza o item no cache individual
    )
    public Optional<EspecialidadeResponseDTO> atualizar(Long id, EspecialidadeRequestDTO dto) {
        System.out.println("Atualizando especialidade de ID " + id + "...");
        return especialidadeRepository.findById(id)
                .map(especialidadeExistente -> {
                    especialidadeExistente.setNome(dto.nome());
                    especialidadeExistente.setDescricao(dto.descricao());
                    Especialidade especialidadeAtualizada = especialidadeRepository.save(especialidadeExistente);
                    return toResponseDTO(especialidadeAtualizada);
                });
    }

    @Caching(
            evict = {
                @CacheEvict(value = "especialidades", allEntries = true), // Limpa a lista inteira
                @CacheEvict(value = "especialidade", key = "#id") // Remove o item específico
            }
    )
    public boolean deletar(Long id) {
        System.out.println("Deletando especialidade de ID " + id + "...");
        if (especialidadeRepository.existsById(id)) {
            especialidadeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    private Especialidade toEntity(EspecialidadeRequestDTO dto) {
        Especialidade especialidade = new Especialidade();
        especialidade.setNome(dto.nome());
        especialidade.setDescricao(dto.descricao());
        return especialidade;
    }

     private EspecialidadeResponseDTO toResponseDTO(Especialidade especialidade) {
        return new EspecialidadeResponseDTO(
                especialidade.getId(),
                especialidade.getNome(),
                especialidade.getDescricao());
    }
}