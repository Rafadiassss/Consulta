package com.example.consulta.controller;

import com.example.consulta.model.Agenda;
import com.example.consulta.service.AgendaService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/agendas")
@Tag(name = "Agenda", description = "Operações para gerenciar agendas")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @GetMapping
    public List<Agenda> listar() {
        return agendaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agenda> buscarPorId(@PathVariable Long id) {
        Optional<Agenda> agenda = agendaService.buscarPorId(id);
        return agenda.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping
    public Agenda salvar(@RequestBody Agenda agenda) {
        return agendaService.salvar(agenda);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        agendaService.deletar(id);
    }
}