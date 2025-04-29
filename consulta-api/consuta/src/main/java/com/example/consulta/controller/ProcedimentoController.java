package com.example.consulta.controller;

import com.example.consulta.model.Procedimento;
import com.example.consulta.service.ProcedimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/procedimentos")
public class ProcedimentoController {

    @Autowired
    private ProcedimentoService procedimentoService;

    @GetMapping
    public List<Procedimento> listar() {
        return procedimentoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Procedimento> buscarPorId(@PathVariable Long id) {
        return procedimentoService.buscarPorId(id);
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
