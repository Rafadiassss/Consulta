package com.example.consulta.controller;

import com.example.consulta.model.Agenda;
import com.example.consulta.model.Procedimento;
import com.example.consulta.service.ProcedimentoService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/procedimentos")
@Tag(name = "Procedimento", description = "Operações para gerenciar procedimentos")
public class ProcedimentoController {

    @Autowired
    private ProcedimentoService procedimentoService;

    @GetMapping
    public List<Procedimento> listar() {
        return procedimentoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Procedimento> buscarPorId(@PathVariable Long id) {
        Optional<Procedimento> procedimento = procedimentoService.buscarPorId(id);
        return procedimento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Procedimento salvar(@RequestBody Procedimento procedimento) {
        return procedimentoService.salvar(procedimento);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        procedimentoService.deletar(id);
    }
}