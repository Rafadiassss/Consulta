package com.example.consulta.service;

import com.example.consulta.dto.PacienteRequestDTO;
import com.example.consulta.enums.TipoUsuario;
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

    // Guarda a lista completa de pacientes no cache "pacientes".
    @Cacheable("pacientes")
    public List<PacienteVO> listarTodos() {
        // Busca todas as entidades e as converte para VOs.
        return pacienteRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    // Guarda um paciente individual no cache "paciente", usando o ID como chave.
    @Cacheable(value = "paciente", key = "#id")
    public Optional<PacienteVO> buscarPorId(Long id) {
        // Busca a entidade pelo ID e a converte para VO, se encontrada.
        return pacienteRepository.findById(id).map(this::toVO);
    }

    // Agrupa operações de cache: invalida a lista e adiciona/atualiza o item salvo.
    @Caching(evict = { @CacheEvict(value = "pacientes", allEntries = true) }, put = {
            @CachePut(value = "paciente", key = "#result.id()") })
    public PacienteVO salvar(PacienteRequestDTO dto) {
        // Converte o DTO recebido para uma entidade.
        Paciente paciente = toEntity(dto);
        // Salva a nova entidade no banco.
        Paciente pacienteSalvo = pacienteRepository.save(paciente);
        // Converte a entidade salva para VO e a retorna.
        return toVO(pacienteSalvo);
    }

    // Agrupa operações de cache: invalida a lista e atualiza o item individual.
    @Caching(evict = { @CacheEvict(value = "pacientes", allEntries = true) }, put = {
            @CachePut(value = "paciente", key = "#id") })
    public Optional<PacienteVO> atualizar(Long id, PacienteRequestDTO dto) {
        // Busca o paciente existente pelo ID.
        return pacienteRepository.findById(id)
                .map(pacienteExistente -> {
                    // Se encontrado, atualiza seus campos com os dados do DTO.
                    pacienteExistente.setNome(dto.nome());
                    pacienteExistente.setUsername(dto.username());
                    pacienteExistente.setEmail(dto.email());
                    pacienteExistente.setTelefone(dto.telefone());
                    pacienteExistente.setDataNascimento(dto.dataNascimento());
                    pacienteExistente.setCpf(dto.cpf());
                    pacienteExistente.setCartaoSus(dto.cartaoSus());
                    // Salva a entidade atualizada.
                    Paciente pacienteAtualizado = pacienteRepository.save(pacienteExistente);
                    // Converte e retorna o VO da entidade atualizada.
                    return toVO(pacienteAtualizado);
                });
    }

    // Agrupa operações de cache: limpa a lista e o item individual.
    @Caching(evict = {
            @CacheEvict(value = "pacientes", allEntries = true),
            @CacheEvict(value = "paciente", key = "#id")
    })
    public boolean deletar(Long id) {
        // Verifica se o registro com o ID fornecido existe.
        if (pacienteRepository.existsById(id)) {
            // Se existir, deleta e retorna true.
            pacienteRepository.deleteById(id);
            return true;
        }
        // Se não existir, retorna false.
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    // Converte uma Entidade 'Paciente' para um 'PacienteVO'.
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

    // Converte um 'PacienteRequestDTO' para uma Entidade 'Paciente'.
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
        // Define o tipo usando o Enum correto (em maiúsculas).
        paciente.setTipo(TipoUsuario.PACIENTE);
        // Retorna a entidade pronta para ser salva.
        return paciente;
    }
}