-- RotaLog - API Frotas Seed Data
-- Dados iniciais para desenvolvimento e testes

-- Veículos
INSERT INTO veiculos (placa, modelo, ano_fabricacao, quilometragem, status, data_cadastro, data_atualizacao) VALUES
('ABC1D23', 'Fiat Fiorino', 2020, 45000, 'ATIVO', '2023-01-15 10:00:00', '2024-02-10 14:30:00'),
('DEF4G56', 'VW Delivery 9.170', 2019, 120000, 'ATIVO', '2023-02-20 09:00:00', '2024-03-01 16:00:00'),
('GHI7J89', 'Mercedes Sprinter', 2021, 32000, 'ATIVO', '2023-03-10 11:00:00', '2024-01-20 10:00:00'),
('JKL0M12', 'Iveco Daily 35S14', 2018, 180000, 'MANUTENCAO', '2023-01-05 08:00:00', '2024-03-15 09:00:00'),
('NOP3Q45', 'Renault Master', 2022, 15000, 'ATIVO', '2023-06-01 14:00:00', '2024-02-28 11:00:00'),
('RST6U78', 'Fiat Ducato', 2017, 210000, 'INATIVO', '2022-08-10 10:00:00', '2024-01-10 15:00:00'),
('VWX9Y01', 'VW Constellation 17.280', 2020, 95000, 'ATIVO', '2023-04-15 09:30:00', '2024-03-10 12:00:00'),
('BCD2E34', 'Scania R450', 2021, 78000, 'ATIVO', '2023-05-20 10:00:00', '2024-03-05 14:00:00'),
('FGH5I67', 'Volvo FH 540', 2019, 150000, 'ATIVO', '2023-02-01 08:00:00', '2024-02-20 16:30:00'),
('JKL8M90', 'Mercedes Actros 2651', 2022, 42000, 'ATIVO', '2023-07-10 11:00:00', '2024-03-12 09:00:00');

-- Motoristas
INSERT INTO motoristas (nome, cnh, categoria_cnh, vencimento_cnh, status, data_cadastro, data_atualizacao) VALUES
('Carlos Eduardo Santos', '12345678901', 'D', '2025-06-15', 'ATIVO', '2023-01-15 10:00:00', '2024-01-10 14:00:00'),
('Ana Paula Oliveira', '23456789012', 'C', '2024-12-20', 'ATIVO', '2023-02-20 09:00:00', '2024-02-15 10:00:00'),
('Roberto Almeida Silva', '34567890123', 'E', '2025-03-10', 'ATIVO', '2023-03-10 11:00:00', '2024-03-01 16:00:00'),
('Fernanda Costa Lima', '45678901234', 'D', '2024-01-05', 'ATIVO', '2023-04-05 08:00:00', '2024-01-05 09:00:00'),
('José Ricardo Pereira', '56789012345', 'C', '2025-09-30', 'ATIVO', '2023-05-15 14:00:00', '2024-02-28 11:00:00'),
('Marcos Vinícius Souza', '67890123456', 'E', '2024-08-20', 'INATIVO', '2023-01-20 10:00:00', '2024-03-10 15:00:00'),
('Patrícia Mendes Rocha', '78901234567', 'D', '2025-11-15', 'ATIVO', '2023-06-01 09:00:00', '2024-03-05 12:00:00'),
('Lucas Gabriel Ferreira', '89012345678', 'C', '2025-04-25', 'ATIVO', '2023-07-10 11:00:00', '2024-02-20 14:00:00');

-- Manutenções
INSERT INTO manutencoes (veiculo_id, tipo_manutencao, data_manutencao, quilometragem_manutencao, custo, descricao, status, data_criacao, data_atualizacao) VALUES
(1, 'PREVENTIVA', '2024-01-15 08:00:00', 40000, 850.00, 'Troca de óleo e filtros', 'CONCLUIDA', '2024-01-10 10:00:00', '2024-01-15 16:00:00'),
(2, 'CORRETIVA', '2024-02-10 09:00:00', 115000, 2500.00, 'Reparo no sistema de freios', 'CONCLUIDA', '2024-02-08 14:00:00', '2024-02-12 11:00:00'),
(4, 'PREVENTIVA', '2024-03-15 08:00:00', 180000, NULL, 'Revisão geral - 180.000 km', 'EM_ANDAMENTO', '2024-03-10 10:00:00', '2024-03-15 09:00:00'),
(1, 'PREVENTIVA', NULL, NULL, 600.00, 'Troca de óleo e filtros - próxima revisão', 'PENDENTE', '2024-03-20 10:00:00', '2024-03-20 10:00:00'),
(6, 'CORRETIVA', '2023-12-20 10:00:00', 205000, 4500.00, 'Motor com problema - veículo desativado', 'CONCLUIDA', '2023-12-15 08:00:00', '2024-01-10 15:00:00'),
(3, 'REVISAO', '2024-02-28 09:00:00', 30000, 450.00, 'Revisão de 30.000 km', 'CONCLUIDA', '2024-02-25 10:00:00', '2024-02-28 14:00:00');

-- FIXME: Sem dados de relacionamento motorista-veículo
-- FIXME: Sem dados de auditoria
-- TODO: Adicionar mais dados para testes de performance
