package com.example.consulta.service;

import com.example.consulta.dto.MedicoRequestDTO;
import com.example.consulta.dto.PacienteRequestDTO;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Usuario;
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

    @Cacheable("usuarios")
    public List<UsuarioVO> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Cacheable(value = "usuario", key = "#id")
    public Optional<UsuarioVO> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(this::toVO);
    }

    @Caching(evict = { @CacheEvict(value = "usuarios", allEntries = true) }, put = {
            @CachePut(value = "usuario", key = "#result.id()") })
    public UsuarioVO salvarMedico(MedicoRequestDTO dto) {
        Medico medico = toMedicoEntity(dto);
        Medico medicoSalvo = usuarioRepository.save(medico);
        return toVO(medicoSalvo);
    }

    @Caching(evict = { @CacheEvict(value = "usuarios", allEntries = true) }, put = {
            @CachePut(value = "usuario", key = "#result.id()") })
    public UsuarioVO salvarPaciente(PacienteRequestDTO dto) {
        Paciente paciente = toPacienteEntity(dto);
        Paciente pacienteSalvo = usuarioRepository.save(paciente);
        return toVO(pacienteSalvo);
    }

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
        String especialidade = null;
        String cpf = null;

        // Verifica o tipo de usuário para preencher os campos específicos.
        if (usuario instanceof Medico) {
            Medico medico = (Medico) usuario;
            crm = medico.getCrm();
            especialidade = medico.getEspecialidade();
        } else if (usuario instanceof Paciente) {
            Paciente paciente = (Paciente) usuario;
            cpf = paciente.getCpf();
        }

        return new UsuarioVO(
                usuario.getId(), usuario.getNome(), usuario.getUsername(),
                usuario.getEmail(), usuario.getTelefone(), usuario.getTipo(),
                usuario.getDataNascimento(), crm, especialidade, cpf);
    }

    private Medico toMedicoEntity(MedicoRequestDTO dto) {
        Medico medico = new Medico();
        medico.setNome(dto.nome());
        medico.setUsername(dto.username());
        medico.setSenha(dto.senha()); // Lembre-se de codificar a senha!
        medico.setEmail(dto.email());
        medico.setTelefone(dto.telefone());
        medico.setCrm(dto.crm());
        medico.setEspecialidade(dto.especialidade());
        medico.setTipo("MEDICO");
        return medico;
    }

    private Paciente toPacienteEntity(PacienteRequestDTO dto) {
        Paciente paciente = new Paciente();
        paciente.setNome(dto.nome());
        paciente.setUsername(dto.username());
        paciente.setSenha(dto.senha());
        paciente.setEmail(dto.email());
        paciente.setTelefone(dto.telefone());
        paciente.setDataNascimento(dto.dataNascimento());
        paciente.setCpf(dto.cpf());
        paciente.setTipo("PACIENTE");
        return paciente;
    }
}