package com.example.consulta.controller;

import com.example.consulta.model.Pagamento;
import com.example.consulta.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public List<Pagamento> listar() {
        return pagamentoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Pagamento> buscarPorId(@PathVariable Long id) {
        return pagamentoService.buscarPorId(id);
    }

    @PostMapping
    public Pagamento salvar(@RequestBody Pagamento pagamento) {
        return pagamentoService.salvar(pagamento);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        pagamentoService.deletar(id);
    }
}
