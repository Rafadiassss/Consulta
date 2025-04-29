package com.example.consulta.controller;

import com.example.consulta.model.Prontuario;
import com.example.consulta.service.ProntuarioService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/prontuarios")
@Tag(name = "Prontuario", description = "Operações para gerenciar prontuários")
public class ProntuarioController {

    @Autowired
    private ProntuarioService prontuarioService;

    @GetMapping
    public List<Prontuario> listar() {
        return prontuarioService.listarTodas();
    }

    @GetMapping("/{id}")
    public Optional<Prontuario> buscarPorId(@PathVariable Long id) {
        return prontuarioService.buscarPorId(id);
    }

    @PostMapping
    public Prontuario salvar(@RequestBody Prontuario prontuario) {
        return prontuarioService.salvar(prontuario);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        prontuarioService.deletar(id);
    }
}
