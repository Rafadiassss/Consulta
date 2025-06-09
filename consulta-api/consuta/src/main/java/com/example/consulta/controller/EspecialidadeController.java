package com.example.consulta.controller;

import com.example.consulta.model.Especialidade;
import com.example.consulta.service.EspecialidadeService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/especialidades")
@Tag(name = "Especialidade", description = "Operações para gerenciar especialidades")
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService especialidadeService;

    @GetMapping
    public List<Especialidade> listar() {
        return especialidadeService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Especialidade> buscarPorId(@PathVariable Long id) {
        Optional<Especialidade> especialidade = especialidadeService.buscarPorId(id);
        return especialidade.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Especialidade salvar(@RequestBody Especialidade especialidade) {
        return especialidadeService.salvar(especialidade);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        especialidadeService.deletar(id);
    }
}