// package com.example.consulta.controller;

// import com.example.consulta.dto.MedicoRequestDTO;
// import com.example.consulta.dto.PacienteRequestDTO;
// import com.example.consulta.hateoas.UsuarioModelAssembler;
// import com.example.consulta.service.UsuarioService;
// import com.example.consulta.vo.UsuarioVO;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.hateoas.EntityModel;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import java.time.LocalDate;
// import java.util.Optional;

// import static org.hamcrest.Matchers.containsString;
// import static org.hamcrest.Matchers.is;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
// import static
// org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
// import static
// org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
// import static
// org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static
// org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(UsuarioController.class)
// @DisplayName("Testes do Controller de Usuários (API)")
// class UsuarioControllerTest {

// @Autowired
// private MockMvc mockMvc;
// @Autowired
// private ObjectMapper objectMapper;

// @MockBean
// private UsuarioService usuarioService;
// @MockBean
// private UsuarioModelAssembler assembler;

// private UsuarioVO medicoVO;
// private UsuarioVO pacienteVO;
// private MedicoRequestDTO medicoRequestDTO;
// private PacienteRequestDTO pacienteRequestDTO;

// @BeforeEach
// void setUp() {
// medicoVO = new UsuarioVO(1L, "Dra. Ana", "ana.med", null, null, null,
// "medico", "12345-SP", "Cardiologia",
// null, null);
// pacienteVO = new UsuarioVO(2L, "Carlos Souza", "carlos.s", null, null, null,
// "paciente", null, null,
// "111.222.333-44", "asasasasasa");

// medicoRequestDTO = new MedicoRequestDTO("Dra. Ana", "ana.med", "senha",
// "ana@email.com", null, "12345-SP", 1L);
// pacienteRequestDTO = new PacienteRequestDTO("Carlos Souza", "carlos.s",
// "senha", null, null,
// LocalDate.now().minusYears(20), "111.222.333-44", null);
// }

// @Test
// @DisplayName("Deve salvar um novo médico e retornar status 201 Created")
// void salvarMedico() throws Exception {
// // Prepara o modelo HATEOAS que o assembler deve retornar.
// EntityModel<UsuarioVO> medicoModel = EntityModel.of(medicoVO,
// linkTo(methodOn(UsuarioController.class).buscarPorId(1L)).withSelfRel());
// // Simula o serviço salvando e retornando o VO.
// when(usuarioService.salvarMedico(any(MedicoRequestDTO.class))).thenReturn(medicoVO);
// // Simula o assembler convertendo o VO.
// when(assembler.toModel(medicoVO)).thenReturn(medicoModel);

// // Executa a requisição POST para o endpoint específico de médicos.
// mockMvc.perform(post("/usuarios/medico")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(medicoRequestDTO)))
// .andExpect(status().isCreated())
// .andExpect(header().string("Location", containsString("/usuarios/1")))
// .andExpect(jsonPath("$.crm", is("12345-SP")));
// }

// @Test
// @DisplayName("Deve salvar um novo paciente e retornar status 201 Created")
// void salvarPaciente() throws Exception {
// // Prepara o modelo HATEOAS.
// EntityModel<UsuarioVO> pacienteModel = EntityModel.of(pacienteVO,
// linkTo(methodOn(UsuarioController.class).buscarPorId(2L)).withSelfRel());
// // Simula o serviço e o assembler.
// when(usuarioService.salvarPaciente(any(PacienteRequestDTO.class))).thenReturn(pacienteVO);
// when(assembler.toModel(pacienteVO)).thenReturn(pacienteModel);

// // Executa a requisição POST para o endpoint específico de pacientes.
// mockMvc.perform(post("/usuarios/paciente")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(pacienteRequestDTO)))
// .andExpect(status().isCreated())
// .andExpect(header().string("Location", containsString("/usuarios/2")))
// .andExpect(jsonPath("$.cpf", is("111.222.333-44")));
// }

// @Test
// @DisplayName("Deve retornar status 404 Not Found ao tentar deletar usuário
// inexistente")
// void deletar_quandoNaoEncontrado() throws Exception {
// // Simula o serviço retornando 'false' para a exclusão.
// when(usuarioService.deletar(99L)).thenReturn(false);

// // Executa a requisição DELETE e verifica o status 404.
// mockMvc.perform(delete("/usuarios/{id}", 99L))
// .andExpect(status().isNotFound());
// }
// }