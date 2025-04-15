package consulta.com.example.demo.controller;

import consulta.com.example.demo.model.Paciente;
import consulta.com.example.demo.service.PacienteService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
@Tag(name = "Paciente", description = "Operações relacionadas aos pacientes")
public class PacienteController {

    @Autowired
    private PacienteService service;

    @GetMapping
    public List<Paciente> listarTodos() {
        return service.listarTodos();
    }

    @PostMapping
    public Paciente cadastrar(@RequestBody Paciente paciente) {
        return service.salvar(paciente);
    }

    @GetMapping("/{id}")
    public Paciente buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Paciente atualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
        return service.atualizar(id, paciente);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
