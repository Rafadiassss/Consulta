package com.example.consulta.controller;

import com.example.consulta.dto.MedicoRequestDTO;
import com.example.consulta.hateoas.MedicoModelAssembler;
import com.example.consulta.service.MedicoService;
import com.example.consulta.vo.MedicoVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicoController.class)
@DisplayName("Testes do Controller de Médicos (API com HATEOAS)")
class MedicoControllerTest {

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private MedicoService medicoService;
        @MockBean
        private MedicoModelAssembler assembler;

        private MedicoVO medicoVO;
        private MedicoRequestDTO medicoRequestDTO;

        @BeforeEach
        void setUp() {
                medicoVO = new MedicoVO(1L, "Dra. Ana", "ana.med", "ana@email.com", null, LocalDate.of(1985, 4, 15),
                                "12345-SP", "Cardiologia");
                medicoRequestDTO = new MedicoRequestDTO("Dra. Ana", "ana.med", "senha", "ana@email.com", null,
                                "12345-SP", 1L);
        }

        @Test
        @DisplayName("Deve buscar médico por ID existente e retornar status 200 OK")
        void buscarPorId_quandoEncontrado() throws Exception {
                // Prepara o modelo HATEOAS que o assembler deve retornar.
                EntityModel<MedicoVO> medicoModel = EntityModel.of(medicoVO,
                                linkTo(methodOn(MedicoController.class).buscarPorId(1L)).withSelfRel());

                // Simula o serviço encontrando o médico.
                when(medicoService.buscarPorId(1L)).thenReturn(Optional.of(medicoVO));
                // Simula o assembler convertendo o VO.
                when(assembler.toModel(medicoVO)).thenReturn(medicoModel);

                // Executa a requisição e verifica a resposta HATEOAS.
                mockMvc.perform(get("/medicos/{id}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.crm", is("12345-SP")))
                                .andExpect(jsonPath("$._links.self.href", containsString("/medicos/1")));
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao buscar por ID inexistente")
        void buscarPorId_quandoNaoEncontrado() throws Exception {
                // Simula o serviço não encontrando o médico.
                when(medicoService.buscarPorId(99L)).thenReturn(Optional.empty());

                // Executa a requisição e espera o 404.
                mockMvc.perform(get("/medicos/{id}", 99L))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve salvar um novo médico e retornar status 201 Created")
        void salvar() throws Exception {
                // Prepara o modelo HATEOAS.
                EntityModel<MedicoVO> medicoModel = EntityModel.of(medicoVO,
                                linkTo(methodOn(MedicoController.class).buscarPorId(1L)).withSelfRel());
                // Simula o serviço e o assembler.
                when(medicoService.salvar(any(MedicoRequestDTO.class))).thenReturn(medicoVO);
                when(assembler.toModel(medicoVO)).thenReturn(medicoModel);

                // Executa a requisição POST.
                mockMvc.perform(post("/medicos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(medicoRequestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(header().string("Location", containsString("/medicos/1")));
        }

        @Test
        @DisplayName("Deve atualizar um médico existente e retornar status 200 OK")
        void atualizar_quandoEncontrado() throws Exception {
                // Prepara o modelo HATEOAS.
                EntityModel<MedicoVO> medicoModel = EntityModel.of(medicoVO);
                // Simula o serviço de atualização e o assembler.
                when(medicoService.atualizar(eq(1L), any(MedicoRequestDTO.class))).thenReturn(Optional.of(medicoVO));
                when(assembler.toModel(medicoVO)).thenReturn(medicoModel);

                // Executa a requisição PUT.
                mockMvc.perform(put("/medicos/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(medicoRequestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)));
        }
}