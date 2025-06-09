package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Usuario;
import com.example.consulta.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario salvarMedico(Medico medico) {
        return usuarioRepository.save(medico);
    }

    public Usuario salvarPaciente(Paciente paciente) {
        return usuarioRepository.save(paciente);
    }

    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> atualizar(Long id, Usuario dadosNovos) {
        // Busca o usuário no banco para garantir que ele existe.
        Optional<Usuario> usuarioExistenteOptional = usuarioRepository.findById(id);

        // Se o usuário não for encontrado, retorna um Optional vazio.
        if (usuarioExistenteOptional.isEmpty()) {
            return Optional.empty();
        }

        // Se encontrou atualiza os campos do objeto existente.
        Usuario usuarioParaAtualizar = usuarioExistenteOptional.get();
        usuarioParaAtualizar.setNome(dadosNovos.getNome());
        usuarioParaAtualizar.setUsername(dadosNovos.getUsername());
        usuarioParaAtualizar.setEmail(dadosNovos.getEmail());
        usuarioParaAtualizar.setTelefone(dadosNovos.getTelefone());
        usuarioParaAtualizar.setSenha(dadosNovos.getSenha());

        // Salva o usuário com os dados atualizados e o retorna.
        return Optional.of(usuarioRepository.save(usuarioParaAtualizar));
    }
}
