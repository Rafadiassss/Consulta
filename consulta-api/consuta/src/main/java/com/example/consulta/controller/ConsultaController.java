package com.example.consulta.controller;

import com.example.consulta.model.Consulta;
import com.example.consulta.service.ConsultaService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/consultas")
@Tag(name = "Consulta", description = "Operações para gerenciar consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @GetMapping
    public List<Consulta> listar() {
        return consultaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Optional<Consulta> buscarPorId(@PathVariable Long id) {
        return consultaService.buscarPorId(id);
    }


    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        consultaService.deletar(id);
    }
    @PostMapping
    public ResponseEntity<Consulta> salvar(@RequestBody Consulta consulta) {
        Consulta novaConsulta = consultaService.salvar(consulta);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConsulta);
    }
}
