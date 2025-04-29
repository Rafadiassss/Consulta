package com.example.consulta.controller;


import com.example.consulta.model.Agenda;
import com.example.consulta.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/agendas")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @GetMapping
    public List<Agenda> listar() {
        return agendaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Optional<Agenda> buscarPorId(@PathVariable Long id) {
        return agendaService.buscarPorId(id);
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
