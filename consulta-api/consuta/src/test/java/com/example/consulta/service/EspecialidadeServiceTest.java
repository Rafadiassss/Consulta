// package com.example.consulta.service;

// import com.example.consulta.model.Especialidade;
// import com.example.consulta.repository.EspecialidadeRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// @DisplayName("Testes da Camada de Serviço de Especialidades")
// class EspecialidadeServiceTest {

//     @Mock
//     private EspecialidadeRepository especialidadeRepository;

//     @InjectMocks
//     private EspecialidadeService especialidadeService;

//     private Especialidade especialidade;

//     @BeforeEach
//     void setUp() {
//         especialidade = new Especialidade();
//         especialidade.setId(1L);
//         especialidade.setNome("Cardiologia");
//     }

//     @Test
//     @DisplayName("Deve listar todas as especialidades")
//     void listarTodas() {
//         when(especialidadeRepository.findAll()).thenReturn(Collections.singletonList(especialidade));
//         List<Especialidade> resultado = especialidadeService.listarTodas();
//         assertThat(resultado).isNotNull().hasSize(1);
//     }

//     @Test
//     @DisplayName("Deve buscar uma especialidade por ID existente")
//     void buscarPorId() {
//         when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(especialidade));
//         Optional<Especialidade> resultado = especialidadeService.buscarPorId(1L);
//         assertThat(resultado).isPresent();
//     }

//     @Test
//     @DisplayName("Deve salvar uma especialidade")
//     void salvar() {
//         when(especialidadeRepository.save(any(Especialidade.class))).thenReturn(especialidade);
//         Especialidade resultado = especialidadeService.salvar(new Especialidade());
//         assertThat(resultado).isNotNull();
//     }

//     @Test
//     @DisplayName("Deve deletar uma especialidade existente e retornar true")
//     void deletar_quandoEncontrado() {
//         // Simula que a especialidade existe.
//         when(especialidadeRepository.existsById(1L)).thenReturn(true);
//         // Configura a chamada ao método 'deleteById'.
//         doNothing().when(especialidadeRepository).deleteById(1L);

//         // Chama o método de serviço.
//         boolean resultado = especialidadeService.deletar(1L);

//         // Verifica se o resultado é 'true' e se 'deleteById' foi chamado.
//         assertThat(resultado).isTrue();
//         verify(especialidadeRepository).deleteById(1L);
//     }

//     @Test
//     @DisplayName("Deve retornar false ao tentar deletar especialidade inexistente")
//     void deletar_quandoNaoEncontrado() {
//         // Simula que a especialidade NÃO existe.
//         when(especialidadeRepository.existsById(99L)).thenReturn(false);

//         // Chama o método de serviço.
//         boolean resultado = especialidadeService.deletar(99L);

//         // Verifica se o resultado é 'false'.
//         assertThat(resultado).isFalse();
//         // Confirma que 'deleteById' NÃO foi chamado.
//         verify(especialidadeRepository, never()).deleteById(anyLong());
//     }

//     @Test
//     @DisplayName("Deve atualizar uma especialidade com sucesso")
//     void atualizar_quandoEncontrado() {
//         Especialidade dadosNovos = new Especialidade();
//         dadosNovos.setNome("Cardiologia Pediátrica");

//         // Simula a busca da entidade existente.
//         when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(especialidade));
//         // Simula a ação de salvar, retornando a entidade com os dados novos.
//         when(especialidadeRepository.save(any(Especialidade.class))).thenReturn(dadosNovos);

//         // Chama o serviço de atualização.
//         Optional<Especialidade> resultado = especialidadeService.atualizar(1L, dadosNovos);

//         // Verifica se a especialidade foi atualizada.
//         assertThat(resultado).isPresent();
//         assertThat(resultado.get().getNome()).isEqualTo("Cardiologia Pediátrica");
//     }

//     @Test
//     @DisplayName("Deve retornar Optional vazio ao tentar atualizar especialidade inexistente")
//     void atualizar_quandoNaoEncontrado() {
//         // Simula a busca retornando vazio.
//         when(especialidadeRepository.findById(99L)).thenReturn(Optional.empty());

//         // Chama o serviço de atualização.
//         Optional<Especialidade> resultado = especialidadeService.atualizar(99L, new Especialidade());

//         // Verifica que o resultado é vazio.
//         assertThat(resultado).isEmpty();
//         // Confirma que o 'save' nunca foi chamado.
//         verify(especialidadeRepository, never()).save(any(Especialidade.class));
//     }
// }