package com.example.consulta.controller;

import com.example.consulta.model.Secretaria;
import com.example.consulta.service.SecretariaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/secretarias")
public class SecretariaController {

    @Autowired
    private SecretariaService secretariaService;

    @GetMapping
    public List<Secretaria> listar() {
        return secretariaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Optional<Secretaria> buscarPorId(@PathVariable Long id) {
        return secretariaService.buscarPorId(id);
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
