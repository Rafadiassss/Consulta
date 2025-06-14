INSERT INTO especialidade (nome, descricao) VALUES
  ('Cardiologia', 'Cuida de doenças do coração e do sistema circulatório.'),
  ('Ortopedia', 'Cuida de lesões e doenças do sistema locomotor (ossos, músculos, articulações).');

INSERT INTO procedimento (nome, descricao, valor) VALUES
  ('Consulta de Rotina', 'Avaliação clínica geral.', 250.00),
  ('Eletrocardiograma (ECG)', 'Exame que avalia a atividade elétrica do coração.', 150.00);

INSERT INTO secretaria (nome, cpf, telefone, email, usuario, senha) VALUES
  ('Carla Mendes', '111.111.111-11', '11988887777', 'carla.mendes@clinica.com', 'carla', 'senha123');

INSERT INTO usuarios (
  dtype, nome, username, senha, tipo, telefone, email, data_nascimento,
  crm, especialidade_id, cpf, cartao_sus
) VALUES
  ('MEDICO', 'Dra. Ana Oliveira', 'ana.oliveira', 'senha_medico1', 'MEDICO',
   '11987654321', 'ana.oliveira@email.com', '1985-04-15', '12345-SP', 1, NULL, NULL),

  ('MEDICO', 'Dr. Roberto Lima', 'roberto.lima', 'senha_medico2', 'MEDICO',
   '21987654321', 'roberto.lima@email.com', '1978-09-10', '67890-RJ', 2, NULL, NULL),

  ('PACIENTE', 'Carlos Souza', 'carlos.souza', 'senha_paciente1', 'PACIENTE',
   '11912345678', 'carlos.souza@email.com', '1990-09-20', NULL, NULL, '123.456.789-00', '987000012345678'),

  ('PACIENTE', 'Mariana Costa', 'mariana.costa', 'senha_paciente2', 'PACIENTE',
   '21987651234', 'mariana.costa@email.com', '1992-12-01', NULL, NULL, '098.765.432-11', '987000087654321');

INSERT INTO prontuario (numero) VALUES
  ('PRT-CS001'),
  ('PRT-MC002');


INSERT INTO consulta (data, status, nome_consulta, paciente_id, medico_id, prontuario_id) VALUES
  ('2025-07-10 10:00:00', 'REALIZADA', 'Consulta Cardiológica de Rotina', 3, 1, 1),
  ('2025-07-11 14:00:00', 'AGENDADA', 'Consulta de Ortopedia - Retorno', 4, 2, 2);

INSERT INTO entrada_prontuario (
  prontuario_id, data_entrada, diagnostico, tratamento, observacoes
) VALUES
  (1, '2025-07-10 10:30:00',
   'Hipertensão Arterial Leve',
   'Prescrito Losartana 50mg, dieta com baixo teor de sódio.',
   'Paciente orientado a monitorar a pressão arterial semanalmente. Retorno em 3 meses.');

INSERT INTO pagamento (
  data_pagamento, valor_pago, forma_pagamento, status
) VALUES
  ('2025-07-10', 250.00, 'CONVENIO', 'CONFIRMADO');

INSERT INTO exame (
  nome, resultado, observacoes, consulta_id
) VALUES
  ('Eletrocardiograma de Repouso',
   'Ritmo sinusal normal, sem alterações significativas.',
   'Realizado durante a consulta.',
   1);

INSERT INTO agenda (data_agendada) VALUES
  ('2025-09-01');

INSERT INTO agenda_horarios (agenda_id, horarios) VALUES
  (1, '08:00:00'),
  (1, '09:00:00'),
  (1, '10:00:00');