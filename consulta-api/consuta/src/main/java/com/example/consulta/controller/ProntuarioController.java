package com.example.consulta.controller;

import com.example.consulta.model.EntradaProntuario;
import com.example.consulta.model.Prontuario;
import com.example.consulta.service.ProntuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prontuarios")
public class ProntuarioController {

    private final ProntuarioService prontuarioService;

    public ProntuarioController(ProntuarioService prontuarioService) {
        this.prontuarioService = prontuarioService;
    }

    @PostMapping("/salvar/{idUsuario}")
    public ResponseEntity<String> salvar(
            @PathVariable Long idUsuario,
            @RequestBody Prontuario prontuario) {

        return prontuarioService.criarProntuario(idUsuario, prontuario);
    }

    @GetMapping("/buscar/{idUsuario}/{idProntuario}")
    public ResponseEntity<?> buscarProntuario(
            @PathVariable Long idUsuario,
            @PathVariable Long idProntuario) {

        return prontuarioService.buscarProntuario(idUsuario, idProntuario);
    }

    @PostMapping("/{idProntuario}/entradas")
    public ResponseEntity<?> adicionarNovaEntrada(
            @PathVariable Long idProntuario,
            @RequestBody EntradaProntuario novaEntrada) {

        return prontuarioService.adicionarEntrada(idProntuario, novaEntrada);
    }
}