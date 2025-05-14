package com.example.consulta.controller;

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

    @PostMapping("/salvar")
    public ResponseEntity<String> salvar(
            @RequestParam Long idUsuario,
            @RequestBody Prontuario prontuario) {

        return prontuarioService.criarProntuario(idUsuario, prontuario);
    }
}
