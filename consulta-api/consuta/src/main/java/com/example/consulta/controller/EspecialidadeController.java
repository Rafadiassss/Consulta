package com.example.consulta.controller;

import com.example.consulta.model.Especialidade;
import com.example.consulta.service.EspecialidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/especialidades")
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService especialidadeService;

    @GetMapping
    public List<Especialidade> listar() {
        return especialidadeService.listarTodas();
    }

    @GetMapping("/{id}")
    public Optional<Especialidade> buscarPorId(@PathVariable Long id) {
        return especialidadeService.buscarPorId(id);
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
