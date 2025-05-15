package com.example.consulta.service;

import com.example.consulta.model.Prontuario;
import com.example.consulta.model.Usuario;
import com.example.consulta.repository.ProtuarioRepository;
import com.example.consulta.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service 
public class ProntuarioService {

    private final ProtuarioRepository prontuarioRepository;
    private final UsuarioRepository usuarioRepository;

    public ProntuarioService(ProtuarioRepository prontuarioRepository, UsuarioRepository usuarioRepository) {
        this.prontuarioRepository = prontuarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ResponseEntity<String> criarProntuario(Long idUsuario, Prontuario prontuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário (médico) não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();

        if (!"MÉDICO".equals(usuario.getTipo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas médicos podem criar prontuários.");
        }

        prontuarioRepository.save(prontuario);

        return ResponseEntity.status(HttpStatus.CREATED).body("Prontuário criado com sucesso.");
    }
    public Usuario getUsuarioById(long id) {
    return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
     public ResponseEntity<?> buscarProntuario(Long idUsuario, Long idProntuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        if (!"MÉDICO".equals(usuario.getTipo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas médicos podem visualizar prontuários.");
        }

        Optional<Prontuario> prontuarioOpt = prontuarioRepository.findById(idProntuario);
        if (prontuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Prontuário não encontrado.");
        }

        return ResponseEntity.ok(prontuarioOpt.get());
    }
}
