package com.example.consulta.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.consulta.model.Consulta;
import com.example.consulta.service.ConsultaService;

class ConsultaControllerTest {

    @InjectMocks
    private ConsultaController consultaController;

    @Mock
    private ConsultaService consultaService;

    private Consulta consulta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        consulta = new Consulta(); // você pode configurar atributos aqui se necessário
        consulta.setId(1L);
    }

    @Test
    void testListarTodas() {
        List<Consulta> consultas = Arrays.asList(consulta);
        when(consultaService.listarTodas()).thenReturn(consultas);

        List<Consulta> resultado = consultaController.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals(consulta, resultado.get(0));
    }

    @Test
    void testBuscarPorId_Existe() {
        when(consultaService.buscarPorId(1L)).thenReturn(Optional.of(consulta));

        ResponseEntity<Consulta> resposta = consultaController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
    }

    @Test
    void testBuscarPorId_NaoExiste() {
        when(consultaService.buscarPorId(2L)).thenReturn(Optional.empty());

        ResponseEntity<Consulta> resposta = consultaController.buscarPorId(2L);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void testSalvar() {
        when(consultaService.salvar(any(Consulta.class))).thenReturn(consulta);

        Consulta resultado = consultaController.salvar(consulta);

        assertNotNull(resultado);
        assertEquals(consulta, resultado);
    }

    @Test
    void testAtualizar_Existe() {
        when(consultaService.atualizar(eq(1L), any(Consulta.class))).thenReturn(consulta);

        ResponseEntity<Consulta> resposta = consultaController.atualizar(1L, consulta);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
    }

    @Test
    void testAtualizar_NaoExiste() {
        when(consultaService.atualizar(eq(1L), any(Consulta.class))).thenReturn(null);

        ResponseEntity<Consulta> resposta = consultaController.atualizar(1L, consulta);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void testDeletar() {
        // não retorna nada, só valida que não lança exceção
        doNothing().when(consultaService).deletar(1L);

        ResponseEntity<Void> resposta = consultaController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(consultaService, times(1)).deletar(1L);
    }
}
