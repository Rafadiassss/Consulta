package com.example.consulta.service;

import com.example.consulta.model.EntradaProntuario;
import com.example.consulta.model.Prontuario;
import com.example.consulta.model.Usuario;
import com.example.consulta.repository.ProntuarioRepository;
import com.example.consulta.repository.UsuarioRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final UsuarioRepository usuarioRepository;

    public ProntuarioService(ProntuarioRepository prontuarioRepository, UsuarioRepository usuarioRepository) {
        this.prontuarioRepository = prontuarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Método para criar um prontuário (com validação de médico)
    @CacheEvict(value = "prontuarios", allEntries = true) // Limpa o cache "prontuarios" na criação
    @CachePut(value = "prontuario", key = "#prontuario.id") // Adiciona o novo prontuário no cache "prontuario"
    public ResponseEntity<String> criarProntuario(Long idUsuario, Prontuario prontuario) {
        System.out.println("Entrou no método criarProntuario");
        System.out.println("ID do Usuário: " + idUsuario);
        System.out.println("Prontuário recebido: " + prontuario);

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);

        if (usuarioOpt.isEmpty()) {
            System.out.println("Usuário não encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário (médico) não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();

        if (!"MEDICO".equals(usuario.getTipo())) {
            System.out.println("Usuário não é médico.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas médicos podem criar prontuários.");
        }

        prontuario.setMedico(usuario);
        prontuarioRepository.save(prontuario);
        System.out.println("Prontuário salvo com ID: " + prontuario.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body("Prontuário criado com sucesso.");
    }

    public Usuario getUsuarioById(long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // Método para buscar prontuário por ID (com validação de médico)
    @Cacheable(value = "prontuario", key = "#idProntuario") // Tenta buscar do cache primeiro
    public ResponseEntity<?> buscarProntuario(Long idUsuario, Long idProntuario) {
        System.out.println("Entrou no método buscarProntuario");
        System.out.println("ID do Usuário: " + idUsuario);
        System.out.println("ID do Prontuário: " + idProntuario);

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            System.out.println("Usuário não encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        if (!"MÉDICO".equals(usuario.getTipo())) {
            System.out.println("Usuário não é médico.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas médicos podem visualizar prontuários.");
        }

        Optional<Prontuario> prontuarioOpt = prontuarioRepository.findById(idProntuario);
        if (prontuarioOpt.isEmpty()) {
            System.out.println("Prontuário não encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Prontuário não encontrado.");
        }

        System.out.println("Prontuário encontrado: " + prontuarioOpt.get());
        return ResponseEntity.ok(prontuarioOpt.get());
    }

    @CacheEvict(value = "prontuarios", allEntries = true) // Invalida o cache da lista de prontuários
    @CachePut(value = "prontuario", key = "#idProntuario") // Atualiza o cache do prontuário específico
    public ResponseEntity<?> adicionarEntrada(Long idProntuario, EntradaProntuario novaEntrada) {
        // Busca o prontuário principal
        Optional<Prontuario> prontuarioOpt = prontuarioRepository.findById(idProntuario);

        if (prontuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Prontuário principal não encontrado.");
        }

        Prontuario prontuario = prontuarioOpt.get();

        // Define a data atual para a nova entrada
        novaEntrada.setDataEntrada(LocalDateTime.now());

        // Adiciona a nova entrada ao histórico do prontuário
        prontuario.adicionarEntrada(novaEntrada);

        // Salva o prontuário com a nova entrada
        Prontuario prontuarioAtualizado = prontuarioRepository.save(prontuario);

        return ResponseEntity.ok(prontuarioAtualizado);
    }
}
