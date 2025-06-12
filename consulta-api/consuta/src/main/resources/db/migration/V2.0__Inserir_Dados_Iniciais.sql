-- Inserção de dados iniciais para desenvolvimento e teste.

-- Insere entidades independentes primeiro
INSERT INTO especialidade (id, nome, descricao) VALUES
(1, 'Cardiologia', 'Cuida de doenças do coração e do sistema circulatório.'),
(2, 'Ortopedia', 'Cuida de lesões e doenças do sistema locomotor (ossos, músculos, articulações).');

INSERT INTO procedimento (id, nome, descricao, valor) VALUES
(1, 'Consulta de Rotina', 'Avaliação clínica geral.', 250.00),
(2, 'Eletrocardiograma (ECG)', 'Exame que avalia a atividade elétrica do coração.', 150.00);

INSERT INTO secretaria (id, nome, cpf, telefone, email, usuario, senha) VALUES
(1, 'Carla Mendes', '111.111.111-11', '11988887777', 'carla.mendes@clinica.com', 'carla', 'senha123');

-- Insere os usuários (médicos e pacientes)
INSERT INTO usuarios (id, dtype, nome, username, senha, tipo, telefone, email, data_nascimento, crm, especialidade_id, cpf, cartao_sus) VALUES
(1, 'MEDICO', 'Dra. Ana Oliveira', 'ana.oliveira', 'senha_medico1', 'MEDICO', '11987654321', 'ana.oliveira@email.com', '1985-04-15', '12345-SP', 1, NULL, NULL),
(2, 'MEDICO', 'Dr. Roberto Lima', 'roberto.lima', 'senha_medico2', 'MEDICO', '21987654321', 'roberto.lima@email.com', '1978-09-10', '67890-RJ', 2, NULL, NULL),
(3, 'PACIENTE', 'Carlos Souza', 'carlos.souza', 'senha_paciente1', 'PACIENTE', '11912345678', 'carlos.souza@email.com', '1990-09-20', NULL, NULL, '123.456.789-00', '987000012345678'),
(4, 'PACIENTE', 'Mariana Costa', 'mariana.costa', 'senha_paciente2', 'PACIENTE', '21987651234', 'mariana.costa@email.com', '1992-12-01', NULL, NULL, '098.765.432-11', '987000087654321');

-- Cria os prontuários para os pacientes
INSERT INTO prontuario (id, numero) VALUES
(1, 'PRT-CS001'), -- Prontuário do Carlos Souza (paciente id 3)
(2, 'PRT-MC002'); -- Prontuário da Mariana Costa (paciente id 4)

-- Cria as consultas, ligando os pacientes, médicos e prontuários
INSERT INTO consulta (id, data, status, nome_consulta, paciente_id, medico_id, prontuario_id) VALUES
(1, '2025-07-10 10:00:00', 'REALIZADA', 'Consulta Cardiológica de Rotina', 3, 1, 1),
(2, '2025-07-11 14:00:00', 'AGENDADA', 'Consulta de Ortopedia - Retorno', 4, 2, 2);

-- Adiciona entradas ao histórico do prontuário do Carlos Souza
INSERT INTO entrada_prontuario (id, prontuario_id, data_entrada, diagnostico, tratamento, observacoes) VALUES
(1, 1, '2025-07-10 10:30:00', 'Hipertensão Arterial Leve', 'Prescrito Losartana 50mg, dieta com baixo teor de sódio.', 'Paciente orientado a monitorar a pressão arterial semanalmente. Retorno em 3 meses.');

-- Adiciona pagamentos para as consultas
INSERT INTO pagamento (id, data_pagamento, valor_pago, forma_pagamento, status, consulta_id) VALUES
(1, '2025-07-10', 250.00, 'CONVENIO', 'CONFIRMADO', 1);

-- Adiciona exames para as consultas
INSERT INTO exame (id, nome, resultado, observacoes, consulta_id) VALUES
(1, 'Eletrocardiograma de Repouso', 'Ritmo sinusal normal, sem alterações significativas.', 'Realizado durante a consulta.', 1);

-- Define horários nas agendas
INSERT INTO agenda (id, data_agendada) VALUES (1, '2025-09-01');
INSERT INTO agenda_horarios (agenda_id, horarios) VALUES
(1, '08:00:00'),
(1, '09:00:00'),
(1, '10:00:00');