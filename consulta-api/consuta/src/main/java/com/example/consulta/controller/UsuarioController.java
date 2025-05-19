package com.example.consulta.controller;

import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Usuario;
import com.example.consulta.service.UsuarioService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuário", description = "Operações para gerenciar usuários")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listarTodos() {
    return usuarioService.listarTodos();
}

    @GetMapping("/{id}")
    public Optional<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @PostMapping
    public Usuario salvar(@RequestBody Usuario usuario) {
        return usuarioService.salvar(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return usuarioService.buscarPorId(id).map(usuario -> {
            usuarioService.deletar(id);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/medico")
    public Usuario salvarMedico(@RequestBody Medico medico) {
        return usuarioService.salvarMedico(medico);
    }

    @PostMapping("/paciente")
    public Usuario salvarPaciente(@RequestBody Paciente paciente) {
        return usuarioService.salvarPaciente(paciente);
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        usuario.setId(id);
        return usuarioService.salvar(usuario);
    }
}
