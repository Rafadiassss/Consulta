package com.example.consulta.service;

import com.example.consulta.dto.SecretariaRequestDTO;
import com.example.consulta.model.Secretaria;
import com.example.consulta.repository.SecretariaRepository;
import com.example.consulta.vo.SecretariaVO;
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
public class SecretariaService {

    @Autowired
    private SecretariaRepository secretariaRepository;

    // Guarda a lista de secretarias no cache "secretarias".
    @Cacheable("secretarias")
    public List<SecretariaVO> listarTodas() {
        // Busca todas as entidades e as converte para VOs.
        return secretariaRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    // Guarda uma secretaria individual no cache "secretaria".
    @Cacheable(value = "secretaria", key = "#id")
    public Optional<SecretariaVO> buscarPorId(Long id) {
        // Busca a entidade pelo ID e a converte para VO.
        return secretariaRepository.findById(id).map(this::toVO);
    }

    // Invalida a lista e adiciona/atualiza a nova secretaria no cache.
    @Caching(evict = { @CacheEvict(value = "secretarias", allEntries = true) }, put = {
            @CachePut(value = "secretaria", key = "#result.id()") })
    public SecretariaVO salvar(SecretariaRequestDTO dto) {
        // Converte o DTO para entidade.
        Secretaria secretaria = toEntity(dto);
        // Salva no banco.
        Secretaria secretariaSalva = secretariaRepository.save(secretaria);
        // Converte para VO e retorna.
        return toVO(secretariaSalva);
    }

    // Invalida a lista e atualiza a secretaria no cache.
    @Caching(evict = { @CacheEvict(value = "secretarias", allEntries = true) }, put = {
            @CachePut(value = "secretaria", key = "#id") })
    public Optional<SecretariaVO> atualizar(Long id, SecretariaRequestDTO dto) {
        // Busca a secretaria existente.
        return secretariaRepository.findById(id)
                .map(secretariaExistente -> {
                    // Atualiza os campos.
                    secretariaExistente.setNome(dto.nome());
                    secretariaExistente.setCpf(dto.cpf());
                    secretariaExistente.setTelefone(dto.telefone());
                    secretariaExistente.setEmail(dto.email());
                    secretariaExistente.setUsuario(dto.usuario());
                    if (dto.senha() != null && !dto.senha().isBlank()) {
                        secretariaExistente.setSenha(dto.senha());
                    }
                    // Salva e retorna o VO atualizado.
                    Secretaria secretariaAtualizada = secretariaRepository.save(secretariaExistente);
                    return toVO(secretariaAtualizada);
                });
    }

    // Limpa os caches ao deletar.
    @Caching(evict = {
            @CacheEvict(value = "secretarias", allEntries = true),
            @CacheEvict(value = "secretaria", key = "#id")
    })
    public boolean deletar(Long id) {
        // Verifica se existe antes de deletar.
        if (secretariaRepository.existsById(id)) {
            secretariaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    private SecretariaVO toVO(Secretaria secretaria) {
        return new SecretariaVO(
                secretaria.getId(),
                secretaria.getNome(),
                secretaria.getCpf(),
                secretaria.getTelefone(),
                secretaria.getEmail(),
                secretaria.getUsuario());
    }

    private Secretaria toEntity(SecretariaRequestDTO dto) {
        Secretaria secretaria = new Secretaria();
        secretaria.setNome(dto.nome());
        secretaria.setCpf(dto.cpf());
        secretaria.setTelefone(dto.telefone());
        secretaria.setEmail(dto.email());
        secretaria.setUsuario(dto.usuario());
        secretaria.setSenha(dto.senha());
        return secretaria;
    }
}