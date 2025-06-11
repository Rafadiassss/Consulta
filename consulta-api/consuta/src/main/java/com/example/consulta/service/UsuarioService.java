package com.example.consulta.service;

import com.example.consulta.dto.MedicoRequestDTO;
import com.example.consulta.dto.PacienteRequestDTO;
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
    private EspecialidadeRepository especialidadeRepository;

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

    // Este método deve estar na sua classe UsuarioService.java

    private UsuarioVO toVO(Usuario usuario) {
        String crm = null;
        String especialidade = null; // Renomeado para evitar conflito com a entidade
        String cpf = null;

        // Verifica o tipo de usuário para preencher os campos específicos.
        if (usuario instanceof Medico) {
            Medico medico = (Medico) usuario;
            crm = medico.getCrm();

            // CORREÇÃO 1: Adiciona uma verificação para evitar NullPointerException.
            // Só tenta pegar o nome se o objeto especialidade não for nulo.
            if (medico.getEspecialidade() != null) {
                especialidade = medico.getEspecialidade().getNome();
            }

        } else if (usuario instanceof Paciente) {
            Paciente paciente = (Paciente) usuario;
            cpf = paciente.getCpf();
        }

        // Cria e retorna o VO com os dados corretos.
        return new UsuarioVO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getTelefone(),
                // CORREÇÃO 2: Usa o tipo dinâmico do usuário, e não um valor fixo.
                usuario.getTipo().name(), // Usamos .name() para pegar o nome do Enum como String
                usuario.getDataNascimento(),
                crm,
                especialidade,
                cpf);
    }

    private Medico toMedicoEntity(MedicoRequestDTO dto) {
        // Lança uma exceção se a especialidade com o ID fornecido não for encontrada.
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

    private Paciente toPacienteEntity(PacienteRequestDTO dto) {
        Paciente paciente = new Paciente();
        paciente.setNome(dto.nome());
        paciente.setUsername(dto.username());
        paciente.setSenha(dto.senha());
        paciente.setEmail(dto.email());
        paciente.setTelefone(dto.telefone());
        paciente.setDataNascimento(dto.dataNascimento());
        paciente.setCpf(dto.cpf());
        paciente.setTipo(TipoUsuario.paciente);
        return paciente;
    }
}