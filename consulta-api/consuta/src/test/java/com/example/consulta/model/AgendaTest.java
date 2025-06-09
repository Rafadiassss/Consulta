package com.example.consulta.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes da Entidade Agenda")
class AgendaTest {

    @Test
    @DisplayName("Deve adicionar um novo horário que não existe na lista")
    void adicionarHorario_deveAdicionarHorarioUnico() {
        // Cria uma instância da agenda e um horário.
        Agenda agenda = new Agenda();
        LocalTime horario = LocalTime.of(9, 0);

        // Garante que a lista de horários começa vazia.
        assertThat(agenda.getHorarios()).isEmpty();

        // Chama o método.
        agenda.adicionarHorario(horario);

        // Verifica se a lista agora contém 1 horário e se é o horário correto.
        assertThat(agenda.getHorarios()).hasSize(1);
        assertThat(agenda.getHorarios()).contains(horario);
    }

    @Test
    @DisplayName("Não deve adicionar um horário que já existe na lista")
    void adicionarHorario_naoDeveAdicionarHorarioDuplicado() {
        // Cria uma instância da agenda e um horário.
        Agenda agenda = new Agenda();
        LocalTime horario = LocalTime.of(10, 30);

        // Adiciona o mesmo horário duas vezes.
        agenda.adicionarHorario(horario);
        agenda.adicionarHorario(horario);

        // Verifica se a lista contém apenas uma instância do horário, pois duplicatas
        // são ignoradas.
        assertThat(agenda.getHorarios()).hasSize(1);
    }

    @Test
    @DisplayName("Deve remover um horário existente da lista")
    void removerHorario_deveRemoverHorarioExistente() {
        // Cria uma agenda e adiciona dois horários diferentes.
        Agenda agenda = new Agenda();
        LocalTime horarioParaManter = LocalTime.of(11, 0);
        LocalTime horarioParaRemover = LocalTime.of(12, 0);
        agenda.adicionarHorario(horarioParaManter);
        agenda.adicionarHorario(horarioParaRemover);

        // Garante que a lista começa com 2 horários.
        assertThat(agenda.getHorarios()).hasSize(2);

        // Chama o método
        agenda.removerHorario(horarioParaRemover);

        // Verifica se a lista agora tem apenas 1 horário e se o horário correto foi
        // removido.
        assertThat(agenda.getHorarios()).hasSize(1);
        assertThat(agenda.getHorarios()).contains(horarioParaManter);
        assertThat(agenda.getHorarios()).doesNotContain(horarioParaRemover);
    }

    @Test
    @DisplayName("Deve retornar uma cópia da lista de horários, protegendo a lista original")
    void listarHorariosDisponiveis_deveRetornarCopiaDaLista() {
        // Cria uma agenda com um horário.
        Agenda agenda = new Agenda();
        LocalTime horarioOriginal = LocalTime.of(15, 0);
        agenda.adicionarHorario(horarioOriginal);

        // Obtém a lista de horários através do método.
        List<LocalTime> listaCopiada = agenda.listarHorariosDisponiveis();

        // Tenta modificar a lista que foi retornada.
        listaCopiada.add(LocalTime.of(16, 0)); // Adiciona um novo horário na cópia
        listaCopiada.clear(); // Limpa a cópia

        // Verifica se a lista original dentro do objeto agenda, permanece intacta.
        // Isso prova que o método retornou uma cópia defensiva, protegendo o estado
        // interno do objeto.
        assertThat(agenda.getHorarios()).hasSize(1);
        assertThat(agenda.getHorarios().get(0)).isEqualTo(horarioOriginal);
    }
}