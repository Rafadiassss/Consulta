package com.example.consulta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.consulta.model.Consulta;
import com.example.consulta.service.ConsultaService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @GetMapping
    public List<Consulta> listarTodas() {
        return consultaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consulta> buscarPorId(@PathVariable Long id) {
        Optional<Consulta> consulta = consultaService.buscarPorId(id);
        return consulta.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Consulta salvar(@RequestBody Consulta consulta) {
        return consultaService.salvar(consulta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consulta> atualizar(@PathVariable Long id, @RequestBody Consulta consultaAtualizada) {
        Consulta consulta = consultaService.atualizar(id, consultaAtualizada);
        if (consulta != null) {
            return ResponseEntity.ok(consulta);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        consultaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
