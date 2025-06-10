package com.example.consulta.controller;

import com.example.consulta.model.Paciente;
import com.example.consulta.service.PacienteService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pacientes")
@Tag(name = "Paciente", description = "Operações para gerenciar pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<Paciente> listar() {
        return pacienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Paciente> buscarPorId(@PathVariable Long id) {
        return pacienteService.buscarPorId(id);
    }

    @PostMapping
   public Paciente salvar(@RequestBody Paciente paciente) {
        System.out.println("CPF: " + paciente.getCpf());
        System.out.println("Cartão SUS: " + paciente.getCartaoSus()); // <- teste aqui
        return pacienteService.salvar(paciente);
}

    @PutMapping("/{id}")
    public Paciente atualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
    return pacienteService.atualizar(id, paciente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
    pacienteService.deletar(id);
    return ResponseEntity.noContent().build();
    }

}

