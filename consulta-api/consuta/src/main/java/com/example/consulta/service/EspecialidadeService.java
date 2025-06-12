package com.example.consulta.service;

import com.example.consulta.dto.EspecialidadeRequestDTO;
import com.example.consulta.model.Especialidade;
import com.example.consulta.repository.EspecialidadeRepository;
import com.example.consulta.repository.MedicoRepository;
import com.example.consulta.vo.EspecialidadeVO; // Importa o VO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EspecialidadeService {

    private final MedicoService medicoService;

    @Autowired
    private EspecialidadeRepository especialidadeRepository;
    @Autowired
    private MedicoRepository medicoRepository;

    EspecialidadeService(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    public List<EspecialidadeVO> listarTodas() {
        return especialidadeRepository.findAll()
                .stream()
                .map(this::toVO) // Converte a Entidade para VO
                .collect(Collectors.toList());
    }

    public Optional<EspecialidadeVO> buscarPorId(Long id) {
        return especialidadeRepository.findById(id)
                .map(this::toVO); // Converte a Entidade para VO
    }

    public EspecialidadeVO salvar(EspecialidadeRequestDTO dto) {
        Especialidade especialidade = toEntity(dto);
        Especialidade especialidadeSalva = especialidadeRepository.save(especialidade);
        // Retorna o VO da entidade salva
        return toVO(especialidadeSalva);
    }

    // O método atualizar recebe o DTO e retorna um DTO.
    @Caching(evict = { @CacheEvict(value = "especialidades", allEntries = true) }, // Limpa o cache da lista
            put = { @CachePut(value = "especialidade", key = "#id") } // Atualiza o item no cache individual
    )
    public Optional<EspecialidadeVO> atualizar(Long id, EspecialidadeRequestDTO dto) {
        System.out.println("Atualizando especialidade de ID " + id + "...");
        return especialidadeRepository.findById(id)
                .map(especialidadeExistente -> {
                    especialidadeExistente.setNome(dto.nome());
                    especialidadeExistente.setDescricao(dto.descricao());
                    Especialidade especialidadeAtualizada = especialidadeRepository.save(especialidadeExistente);
                    return toVO(especialidadeAtualizada);
                });
    }

    @Caching(evict = {
            @CacheEvict(value = "especialidades", allEntries = true), // Limpa a lista inteira
            @CacheEvict(value = "especialidade", key = "#id") // Remove o item específico
    })
    public boolean deletar(Long id) {
        // Primeiro verifica se essa especialidade possui um medico vinculada a ela.
        if (medicoRepository.existsByEspecialidadeId(id)) {
            // Se tiver, lança uma exceção informando que não é possivel deletar uma
            // especialidade vinculada a um medico.
            throw new IllegalStateException("Não é possivel deletar uma especialidade vinculada a um médico!!");
        }
        System.out.println("Deletando especialidade de ID " + id + "...");
        // Agora, se essa especialidade existir com esse ID e não tiver nenhum medico
        // vinculado a ela, então pode deletar
        if (especialidadeRepository.existsById(id)) {
            especialidadeRepository.deleteById(id);
            // Retorna verdadeiro já que o metodo é um boolean
            return true;
        }
        // Se não conseguir deletar, então retorna um 'false' pro boolean
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    private Especialidade toEntity(EspecialidadeRequestDTO dto) {
        Especialidade especialidade = new Especialidade();
        especialidade.setNome(dto.nome());
        especialidade.setDescricao(dto.descricao());
        return especialidade;
    }

    private EspecialidadeVO toVO(Especialidade especialidade) {
        return new EspecialidadeVO(
                especialidade.getId(),
                especialidade.getNome(),
                especialidade.getDescricao());
    }
}
