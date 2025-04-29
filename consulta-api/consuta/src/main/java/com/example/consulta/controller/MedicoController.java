package com.example.consulta.controller;

import com.example.consulta.model.Medico;
import com.example.consulta.repository.MedicoRepository;
import com.example.consulta.service.MedicoService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/medicos")
@Tag(name = "Medico", description = "Operações para gerenciar médicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;
    @Autowired MedicoRepository medicoRepository;

    @GetMapping
    public List<Medico> listar() {
        return medicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medico> getMedicoById(@PathVariable Long id) {
        Medico medico = medicoRepository.findById(id).orElse(null);
            if (medico == null) {
                return ResponseEntity.notFound().build();
             }
        return ResponseEntity.ok(medico);
}

    @PostMapping
    public Medico salvar(@RequestBody Medico medico) {
        return medicoService.salvar(medico);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        medicoService.deletar(id);
    }
}
