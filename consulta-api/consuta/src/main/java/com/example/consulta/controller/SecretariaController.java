package com.example.consulta.controller;

import com.example.consulta.model.Secretaria;
import com.example.consulta.service.SecretariaService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/secretarias")
@Tag(name = "Secretaria", description = "Operações para gerenciar secretarias")
public class SecretariaController {

    @Autowired
    private SecretariaService secretariaService;

    @GetMapping
    public List<Secretaria> listar() {
        return secretariaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Secretaria> buscarPorId(@PathVariable Long id) {
        Optional<Secretaria> secretaria = secretariaService.buscarPorId(id);
        return secretaria.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Secretaria salvar(@RequestBody Secretaria secretaria) {
        return secretariaService.salvar(secretaria);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        secretariaService.deletar(id);
    }
}
