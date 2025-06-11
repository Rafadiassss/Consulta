package com.example.consulta.service;

import com.example.consulta.dto.MedicoRequestDTO;
import com.example.consulta.enums.TipoUsuario;
import com.example.consulta.model.Especialidade;
import com.example.consulta.model.Medico;
import com.example.consulta.repository.EspecialidadeRepository;
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
    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    // Guarda a lista de médicos no cache "medicos".
    @Cacheable("medicos")
    public List<MedicoVO> listarTodos() {
        return medicoRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    // Guarda um médico individual no cache "medico" usando o ID como chave.
    @Cacheable(value = "medico", key = "#id")
    public Optional<MedicoVO> buscarPorId(Long id) {
        return medicoRepository.findById(id).map(this::toVO);
    }

    // Invalida o cache da lista e adiciona/atualiza o novo médico no cache
    // individual.
    @Caching(evict = { @CacheEvict(value = "medicos", allEntries = true) }, put = {
            @CachePut(value = "medico", key = "#result.id()") })
    public MedicoVO salvar(MedicoRequestDTO dto) {
        // Converte o DTO para a entidade, resolvendo a dependência da especialidade.
        Medico medico = toEntity(dto);
        // Salva a nova entidade no banco.
        Medico medicoSalvo = medicoRepository.save(medico);
        // Converte a entidade salva para VO e a retorna.
        return toVO(medicoSalvo);
    }

    // Invalida o cache da lista e atualiza o médico no cache individual.
    @Caching(evict = { @CacheEvict(value = "medicos", allEntries = true) }, put = {
            @CachePut(value = "medico", key = "#id") })
    public Optional<MedicoVO> atualizar(Long id, MedicoRequestDTO dto) {
        // Busca o médico existente pelo ID.
        return medicoRepository.findById(id)
                .map(medicoExistente -> {
                    // Busca a entidade Especialidade pelo ID recebido no DTO.
                    Especialidade especialidade = especialidadeRepository.findById(dto.especialidadeId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Especialidade com ID " + dto.especialidadeId() + " não encontrada."));

                    // Atualiza os campos do médico existente.
                    medicoExistente.setNome(dto.nome());
                    medicoExistente.setUsername(dto.username());
                    medicoExistente.setEmail(dto.email());
                    medicoExistente.setTelefone(dto.telefone());
                    medicoExistente.setCrm(dto.crm());
                    medicoExistente.setEspecialidade(especialidade);

                    // Salva a entidade atualizada.
                    Medico medicoAtualizado = medicoRepository.save(medicoExistente);
                    // Converte para VO e retorna.
                    return toVO(medicoAtualizado);
                });
    }

    // Invalida o cache da lista e remove o item específico do cache individual.
    @Caching(evict = {
            @CacheEvict(value = "medicos", allEntries = true),
            @CacheEvict(value = "medico", key = "#id")
    })
    public boolean deletar(Long id) {
        // Verifica se o registro existe antes de deletar.
        if (medicoRepository.existsById(id)) {
            medicoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    // Converte uma Entidade 'Medico' para um 'MedicoVO'.
    private MedicoVO toVO(Medico medico) {
        // Obtém o nome da especialidade associada de forma segura.
        String nomeEspecialidade = (medico.getEspecialidade() != null) ? medico.getEspecialidade().getNome() : null;
        return new MedicoVO(
                medico.getId(),
                medico.getNome(),
                medico.getUsername(),
                medico.getEmail(),
                medico.getTelefone(),
                medico.getDataNascimento(),
                medico.getCrm(),
                nomeEspecialidade);
    }

    // Converte um 'MedicoRequestDTO' para uma Entidade 'Medico'.
    private Medico toEntity(MedicoRequestDTO dto) {
        // Busca a entidade Especialidade para criar a associação.
        Especialidade especialidade = especialidadeRepository.findById(dto.especialidadeId())
                .orElseThrow(() -> new RuntimeException(
                        "Especialidade com ID " + dto.especialidadeId() + " não encontrada."));

        Medico medico = new Medico();
        medico.setNome(dto.nome());
        medico.setUsername(dto.username());
        medico.setSenha(dto.senha());
        medico.setEmail(dto.email());
        medico.setTelefone(dto.telefone());
        medico.setCrm(dto.crm());
        medico.setEspecialidade(especialidade);
        medico.setTipo(TipoUsuario.medico);
        return medico;
    }
}