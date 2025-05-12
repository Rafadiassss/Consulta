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
}
