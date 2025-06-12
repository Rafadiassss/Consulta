package com.example.consulta.service;

import com.example.consulta.dto.MedicoRequestDTO;
import com.example.consulta.dto.PacienteRequestDTO;
import com.example.consulta.dto.UsuarioUpdateRequestDTO;
import com.example.consulta.enums.TipoUsuario;
import com.example.consulta.model.Especialidade;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Usuario;
import com.example.consulta.repository.EspecialidadeRepository;
import com.example.consulta.repository.UsuarioRepository;
import com.example.consulta.vo.UsuarioVO;
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
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EspecialidadeRepository especialidadeRepository; // Necessário para criar o Médico

    // Guarda a lista de usuários no cache "usuarios".
    @Cacheable("usuarios")
    public List<UsuarioVO> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    // Guarda um usuário individual no cache "usuario".
    @Cacheable(value = "usuario", key = "#id")
    public Optional<UsuarioVO> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(this::toVO);
    }

    // Invalida a lista e adiciona/atualiza o novo médico no cache.
    @Caching(evict = { @CacheEvict(value = "usuarios", allEntries = true) }, put = {
            @CachePut(value = "usuario", key = "#result.id()") })
    public UsuarioVO salvarMedico(MedicoRequestDTO dto) {
        Medico medico = toMedicoEntity(dto);
        Medico medicoSalvo = usuarioRepository.save(medico);
        return toVO(medicoSalvo);
    }

    // Invalida a lista e adiciona/atualiza o novo paciente no cache.
    @Caching(evict = { @CacheEvict(value = "usuarios", allEntries = true) }, put = {
            @CachePut(value = "usuario", key = "#result.id()") })
    public UsuarioVO salvarPaciente(PacienteRequestDTO dto) {
        Paciente paciente = toPacienteEntity(dto);
        Paciente pacienteSalvo = usuarioRepository.save(paciente);
        return toVO(pacienteSalvo);
    }

    // Invalida a lista e atualiza o usuário no cache.
    @Caching(evict = { @CacheEvict(value = "usuarios", allEntries = true) }, put = {
            @CachePut(value = "usuario", key = "#id") })
    public Optional<UsuarioVO> atualizar(Long id, UsuarioUpdateRequestDTO dto) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    // Atualiza apenas os campos permitidos.
                    usuarioExistente.setNome(dto.nome());
                    usuarioExistente.setUsername(dto.username());
                    usuarioExistente.setEmail(dto.email());
                    usuarioExistente.setTelefone(dto.telefone());
                    Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
                    return toVO(usuarioAtualizado);
                });
    }

    // Limpa os caches ao deletar.
    @Caching(evict = {
            @CacheEvict(value = "usuarios", allEntries = true),
            @CacheEvict(value = "usuario", key = "#id")
    })
    public boolean deletar(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    private UsuarioVO toVO(Usuario usuario) {
        String crm = null;
        String nomeEspecialidade = null;
        String cpf = null;
        String cartaoSus = null;

        if (usuario instanceof Medico medico) {
            crm = medico.getCrm();
            if (medico.getEspecialidade() != null) {
                nomeEspecialidade = medico.getEspecialidade().getNome();
            }
        } else if (usuario instanceof Paciente paciente) {
            cpf = paciente.getCpf();
            cartaoSus = paciente.getCartaoSus();
        }
        return new UsuarioVO(
                usuario.getId(), usuario.getNome(), usuario.getUsername(),
                usuario.getEmail(), usuario.getTelefone(),
                usuario.getTipo() != null ? usuario.getTipo().name() : null,
                usuario.getDataNascimento(), crm, nomeEspecialidade, cpf, cartaoSus);
    }

    private Medico toMedicoEntity(MedicoRequestDTO dto) {
        Especialidade especialidade = especialidadeRepository.findById(dto.especialidadeId())
                .orElseThrow(() -> new RuntimeException(
                        "Especialidade com ID " + dto.especialidadeId() + " não encontrada."));

        Medico medico = new Medico();
        // ... (código do toEntity do Medico que já fizemos) ...
        medico.setNome(dto.nome());
        medico.setUsername(dto.username());
        medico.setSenha(dto.senha()); // Lembre-se de codificar a senha!
        medico.setEmail(dto.email());
        medico.setTelefone(dto.telefone());
        medico.setCrm(dto.crm());
        medico.setEspecialidade(especialidade);
        medico.setTipo(TipoUsuario.medico);
        return medico;
    }

    private Paciente toPacienteEntity(PacienteRequestDTO dto) {
        Paciente paciente = new Paciente();
        // ... (código do toEntity do Paciente que já fizemos) ...
        paciente.setNome(dto.nome());
        paciente.setUsername(dto.username());
        paciente.setSenha(dto.senha());
        paciente.setEmail(dto.email());
        paciente.setTelefone(dto.telefone());
        paciente.setDataNascimento(dto.dataNascimento());
        paciente.setCpf(dto.cpf());
        paciente.setCartaoSus(dto.cartaoSus());
        paciente.setTipo(TipoUsuario.paciente);
        return paciente;
    }
}