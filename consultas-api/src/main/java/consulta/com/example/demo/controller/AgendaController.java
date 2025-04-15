package consulta.com.example.demo.controller;

import consulta.com.example.demo.model.Agenda;
import consulta.com.example.demo.service.AgendaService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendas")
@Tag(name = "Agenda", description = "Operações para gerenciar agendas")
public class AgendaController {

    @Autowired
    private AgendaService service;

    @GetMapping
    public List<Agenda> listarTodas() {
        return service.listarTodas();
    }

    @PostMapping
    public Agenda cadastrar(@RequestBody Agenda agenda) {
        return service.salvar(agenda);
    }

    @GetMapping("/{id}")
    public Agenda buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Agenda atualizar(@PathVariable Long id, @RequestBody Agenda agenda) {
        return service.atualizar(id, agenda);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
