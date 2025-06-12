-- Isso acontece por causa dos relacionamentos entre as tabelas
-- Garante que o próximo ID gerado pelo banco será maior que o último ID inserido manualmente.
SELECT setval('especialidade_id_seq', (SELECT MAX(id) FROM especialidade));
SELECT setval('procedimento_id_seq', (SELECT MAX(id) FROM procedimento));
SELECT setval('secretaria_id_seq', (SELECT MAX(id) FROM secretaria));
SELECT setval('usuarios_id_seq', (SELECT MAX(id) FROM usuarios));
SELECT setval('prontuario_id_seq', (SELECT MAX(id) FROM prontuario));
SELECT setval('entrada_prontuario_id_seq', (SELECT MAX(id) FROM entrada_prontuario));
SELECT setval('consulta_id_seq', (SELECT MAX(id) FROM consulta));
SELECT setval('pagamento_id_seq', (SELECT MAX(id) FROM pagamento));
SELECT setval('exame_id_seq', (SELECT MAX(id) FROM exame));
SELECT setval('agenda_id_seq', (SELECT MAX(id) FROM agenda));