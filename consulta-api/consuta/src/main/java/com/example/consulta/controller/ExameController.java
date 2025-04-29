package com.example.consulta.controller;

import com.example.consulta.model.Exame;
import com.example.consulta.service.ExameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/exames")
public class ExameController {

    @Autowired
    private ExameService exameService;

    @GetMapping
    public List<Exame> listar() {
        return exameService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Exame> buscarPorId(@PathVariable Long id) {
        return exameService.buscarPorId(id);
    }

    @PostMapping
    public Exame salvar(@RequestBody Exame exame) {
        return exameService.salvar(exame);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        exameService.deletar(id);
    }
}
