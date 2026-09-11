-- RotaLog - API Entregas - Script de População do Banco
-- Executar após o migration.sql
-- Este script é idempotente (pode ser executado múltiplas vezes)

SET search_path TO entregas;

-- Limpar dados existentes (ordem importa por causa de dependências)
TRUNCATE TABLE rastreamentos RESTART IDENTITY CASCADE;
TRUNCATE TABLE entregas RESTART IDENTITY CASCADE;

-- ============================================================
-- ENTREGAS (20 registros com dados realistas de São Paulo)
-- ============================================================

INSERT INTO entregas (numero_pedido, veiculo_placa, motorista_id, origem_endereco, origem_lat, origem_lng, destino_endereco, destino_lat, destino_lng, peso_kg, distancia_km, tempo_estimado_minutos, status, observacoes, data_criacao, data_atualizacao, data_coleta, data_entrega) VALUES

-- Entregas ENTREGUES (5)
('PED-2024-001', 'ABC1D23', 1, 'Rua Augusta, 1500 - Consolação, São Paulo', -23.5558, -46.6622, 'Av. Paulista, 1000 - Bela Vista, São Paulo', -23.5631, -46.6565, 15.50, 3.2, 15, 'ENTREGUE', 'Entrega expressa - documentos', '2024-01-15 08:00:00', '2024-01-15 10:30:00', '2024-01-15 09:00:00', '2024-01-15 10:30:00'),
('PED-2024-002', 'DEF4G56', 2, 'Av. Brasil, 500 - Jardim América, São Paulo', -23.5505, -46.6333, 'Rua Oscar Freire, 200 - Pinheiros, São Paulo', -23.5622, -46.6720, 45.00, 8.5, 30, 'ENTREGUE', 'Caixa frágil - eletrônicos', '2024-01-20 10:00:00', '2024-01-20 14:00:00', '2024-01-20 11:00:00', '2024-01-20 14:00:00'),
('PED-2024-011', 'ABC1D23', 1, 'Rua Pamplona, 300 - Jardins, São Paulo', -23.5670, -46.6540, 'Av. Nove de Julho, 2000 - Bela Vista, São Paulo', -23.5590, -46.6510, 8.00, 2.1, 10, 'ENTREGUE', NULL, '2024-02-01 08:30:00', '2024-02-01 09:45:00', '2024-02-01 08:45:00', '2024-02-01 09:45:00'),
('PED-2024-012', 'JKL3M45', 4, 'Av. Ibirapuera, 3000 - Moema, São Paulo', -23.6010, -46.6620, 'Rua Domingos de Morais, 800 - Vila Mariana, São Paulo', -23.5980, -46.6380, 32.00, 5.4, 20, 'ENTREGUE', 'Entrega agendada - manhã', '2024-02-05 07:00:00', '2024-02-05 09:30:00', '2024-02-05 07:30:00', '2024-02-05 09:30:00'),
('PED-2024-013', 'DEF4G56', 2, 'Rua Vergueiro, 1500 - Liberdade, São Paulo', -23.5720, -46.6360, 'Av. Liberdade, 500 - Liberdade, São Paulo', -23.5580, -46.6340, 12.00, 1.8, 8, 'ENTREGUE', 'Entrega rápida - alimentos', '2024-02-10 11:00:00', '2024-02-10 12:15:00', '2024-02-10 11:20:00', '2024-02-10 12:15:00'),

-- Entregas EM_TRANSITO (5)
('PED-2024-003', 'GHI7J89', 3, 'Rua Consolação, 800 - Consolação, São Paulo', -23.5510, -46.6600, 'Av. Faria Lima, 3000 - Itaim Bibi, São Paulo', -23.5874, -46.6814, 22.30, 6.1, 25, 'EM_TRANSITO', 'Frágil - manusear com cuidado', '2024-02-10 09:00:00', '2024-02-10 11:00:00', '2024-02-10 10:30:00', NULL),
('PED-2024-007', 'VWX9Y01', 5, 'Av. Brigadeiro Faria Lima, 1000 - Pinheiros, São Paulo', -23.5740, -46.6890, 'Rua Funchal, 400 - Vila Olímpia, São Paulo', -23.5860, -46.6930, 5.00, 1.8, 10, 'EM_TRANSITO', 'Documento urgente - prioridade alta', '2024-03-01 07:00:00', '2024-03-01 08:00:00', '2024-03-01 07:30:00', NULL),
('PED-2024-014', 'ABC1D23', 1, 'Rua Teodoro Sampaio, 500 - Pinheiros, São Paulo', -23.5560, -46.6810, 'Av. Pedroso de Morais, 1200 - Pinheiros, São Paulo', -23.5630, -46.6900, 18.00, 3.5, 15, 'EM_TRANSITO', 'Pacote médio - roupas', '2024-03-10 10:00:00', '2024-03-10 10:45:00', '2024-03-10 10:30:00', NULL),
('PED-2024-015', 'GHI7J89', 3, 'Av. Santo Amaro, 1000 - Santo Amaro, São Paulo', -23.6300, -46.6700, 'Rua Joaquim Floriano, 800 - Itaim Bibi, São Paulo', -23.5820, -46.6780, 55.00, 8.2, 35, 'EM_TRANSITO', 'Carga volumosa - mobiliário', '2024-03-12 08:00:00', '2024-03-12 09:30:00', '2024-03-12 09:00:00', NULL),
('PED-2024-016', 'JKL3M45', 4, 'Rua Cardeal Arcoverde, 2000 - Pinheiros, São Paulo', -23.5530, -46.6850, 'Av. Rebouças, 3500 - Jardins, São Paulo', -23.5680, -46.6730, 10.00, 2.8, 12, 'EM_TRANSITO', NULL, '2024-03-14 14:00:00', '2024-03-14 14:30:00', '2024-03-14 14:20:00', NULL),

-- Entregas ATRIBUIDAS (3)
('PED-2024-004', 'ABC1D23', 1, 'Rua Haddock Lobo, 400 - Jardins, São Paulo', -23.5560, -46.6680, 'Av. Rebouças, 1200 - Pinheiros, São Paulo', -23.5640, -46.6730, 8.00, 2.5, 12, 'ATRIBUIDA', NULL, '2024-02-15 14:00:00', '2024-02-15 14:30:00', NULL, NULL),
('PED-2024-008', 'BCD2E34', 7, 'Rua Bela Cintra, 700 - Consolação, São Paulo', -23.5570, -46.6660, 'Av. Angélica, 2000 - Higienópolis, São Paulo', -23.5430, -46.6580, 18.50, 4.2, 18, 'ATRIBUIDA', 'Entregar no 12o andar', '2024-03-05 09:00:00', '2024-03-05 10:00:00', NULL, NULL),
('PED-2024-017', 'VWX9Y01', 5, 'Av. Europa, 500 - Jardim Europa, São Paulo', -23.5780, -46.6770, 'Rua Groenlândia, 300 - Jardim América, São Paulo', -23.5710, -46.6700, 6.50, 1.5, 8, 'ATRIBUIDA', 'Envelope - assinatura obrigatória', '2024-03-15 08:00:00', '2024-03-15 08:15:00', NULL, NULL),

-- Entregas PENDENTES (5)
('PED-2024-005', NULL, NULL, 'Av. Ipiranga, 200 - República, São Paulo', -23.5430, -46.6420, 'Rua da Mooca, 1500 - Mooca, São Paulo', -23.5580, -46.6010, 120.00, 12.3, 40, 'PENDENTE', 'Carga pesada - necessita veículo grande', '2024-02-20 08:00:00', '2024-02-20 08:00:00', NULL, NULL),
('PED-2024-006', NULL, NULL, 'Rua Vergueiro, 3000 - Vila Mariana, São Paulo', -23.5880, -46.6350, 'Av. Santo Amaro, 500 - Santo Amaro, São Paulo', -23.6200, -46.6650, 35.00, 9.8, 35, 'PENDENTE', NULL, '2024-02-25 10:00:00', '2024-02-25 10:00:00', NULL, NULL),
('PED-2024-010', NULL, NULL, 'Rua Teodoro Sampaio, 1200 - Pinheiros, São Paulo', -23.5530, -46.6810, 'Av. Sumaré, 300 - Sumaré, São Paulo', -23.5350, -46.6780, 28.00, 3.5, 15, 'PENDENTE', 'Agendar para período da manhã', '2024-03-15 08:00:00', '2024-03-15 08:00:00', NULL, NULL),
('PED-2024-018', NULL, NULL, 'Rua da Consolação, 2500 - Consolação, São Paulo', -23.5540, -46.6620, 'Av. Paulista, 2200 - Bela Vista, São Paulo', -23.5560, -46.6620, 3.00, 0.8, 5, 'PENDENTE', 'Entrega simples - envelope', '2024-03-16 09:00:00', '2024-03-16 09:00:00', NULL, NULL),
('PED-2024-019', NULL, NULL, 'Av. Brigadeiro Luís Antônio, 4000 - Jardins, São Paulo', -23.5750, -46.6550, 'Rua Tutóia, 600 - Paraíso, São Paulo', -23.5820, -46.6450, 42.00, 4.5, 20, 'PENDENTE', 'Caixa grande - material de escritório', '2024-03-17 07:00:00', '2024-03-17 07:00:00', NULL, NULL),

-- Entregas CANCELADAS (2)
('PED-2024-009', 'FGH5I67', 3, 'Av. Interlagos, 5000 - Interlagos, São Paulo', -23.6800, -46.6750, 'Rua Domingos de Morais, 800 - Vila Mariana, São Paulo', -23.5980, -46.6380, 65.00, 15.5, 50, 'CANCELADA', 'Cancelado pelo cliente - endereço incorreto', '2024-03-10 11:00:00', '2024-03-10 14:00:00', NULL, NULL),
('PED-2024-020', 'DEF4G56', 2, 'Rua Girassol, 200 - Vila Madalena, São Paulo', -23.5530, -46.6920, 'Av. Henrique Schaumann, 800 - Pinheiros, São Paulo', -23.5620, -46.6830, 15.00, 2.2, 10, 'CANCELADA', 'Cliente não encontrado no endereço', '2024-03-18 10:00:00', '2024-03-18 12:00:00', '2024-03-18 10:30:00', NULL);


-- ============================================================
-- RASTREAMENTOS (eventos para cada entrega)
-- ============================================================

-- PED-2024-001 (ENTREGUE - fluxo completo)
INSERT INTO rastreamentos (entrega_id, latitude, longitude, evento, descricao, data_evento) VALUES
(1, -23.5558, -46.6622, 'PEDIDO_CRIADO', 'Pedido PED-2024-001 criado no sistema', '2024-01-15 08:00:00'),
(1, -23.5558, -46.6622, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo ABC1D23 - Motorista João Silva', '2024-01-15 08:30:00'),
(1, -23.5558, -46.6622, 'COLETA_REALIZADA', 'Pacote coletado na origem', '2024-01-15 09:00:00'),
(1, -23.5590, -46.6590, 'POSICAO_ATUALIZADA', 'Em trânsito - Rua da Consolação', '2024-01-15 09:15:00'),
(1, -23.5610, -46.6575, 'POSICAO_ATUALIZADA', 'Em trânsito - Av. Paulista próximo ao MASP', '2024-01-15 09:45:00'),
(1, -23.5631, -46.6565, 'ENTREGA_REALIZADA', 'Entregue com sucesso - Recebido por Maria', '2024-01-15 10:30:00'),

-- PED-2024-002 (ENTREGUE)
(2, -23.5505, -46.6333, 'PEDIDO_CRIADO', 'Pedido PED-2024-002 criado no sistema', '2024-01-20 10:00:00'),
(2, -23.5505, -46.6333, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo DEF4G56 - Motorista Ana Costa', '2024-01-20 10:30:00'),
(2, -23.5505, -46.6333, 'COLETA_REALIZADA', 'Pacote coletado - eletrônicos frágeis', '2024-01-20 11:00:00'),
(2, -23.5560, -46.6520, 'POSICAO_ATUALIZADA', 'Em trânsito - Av. Rebouças', '2024-01-20 12:00:00'),
(2, -23.5622, -46.6720, 'ENTREGA_REALIZADA', 'Entregue com sucesso', '2024-01-20 14:00:00'),

-- PED-2024-003 (EM_TRANSITO - com posições atualizadas)
(3, -23.5510, -46.6600, 'PEDIDO_CRIADO', 'Pedido PED-2024-003 criado no sistema', '2024-02-10 09:00:00'),
(3, -23.5510, -46.6600, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo GHI7J89 - Motorista Carlos Lima', '2024-02-10 09:30:00'),
(3, -23.5510, -46.6600, 'COLETA_REALIZADA', 'Pacote coletado - frágil', '2024-02-10 10:30:00'),
(3, -23.5580, -46.6650, 'POSICAO_ATUALIZADA', 'Em trânsito - Rua Augusta', '2024-02-10 10:45:00'),
(3, -23.5650, -46.6700, 'POSICAO_ATUALIZADA', 'Em trânsito - Av. Rebouças', '2024-02-10 11:00:00'),
(3, -23.5750, -46.6760, 'POSICAO_ATUALIZADA', 'Em trânsito - próximo ao Shopping Iguatemi', '2024-02-10 11:15:00'),

-- PED-2024-007 (EM_TRANSITO)
(7, -23.5740, -46.6890, 'PEDIDO_CRIADO', 'Pedido PED-2024-007 criado - documento urgente', '2024-03-01 07:00:00'),
(7, -23.5740, -46.6890, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo VWX9Y01 - Motorista Pedro Souza', '2024-03-01 07:15:00'),
(7, -23.5740, -46.6890, 'COLETA_REALIZADA', 'Documento coletado', '2024-03-01 07:30:00'),
(7, -23.5800, -46.6910, 'POSICAO_ATUALIZADA', 'Em trânsito - Av. Faria Lima', '2024-03-01 07:45:00'),

-- PED-2024-014 (EM_TRANSITO)
(8, -23.5560, -46.6810, 'PEDIDO_CRIADO', 'Pedido PED-2024-014 criado', '2024-03-10 10:00:00'),
(8, -23.5560, -46.6810, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo ABC1D23 - Motorista João Silva', '2024-03-10 10:15:00'),
(8, -23.5560, -46.6810, 'COLETA_REALIZADA', 'Pacote coletado', '2024-03-10 10:30:00'),
(8, -23.5590, -46.6850, 'POSICAO_ATUALIZADA', 'Em trânsito - Rua Teodoro Sampaio', '2024-03-10 10:45:00'),

-- PED-2024-015 (EM_TRANSITO)
(9, -23.6300, -46.6700, 'PEDIDO_CRIADO', 'Pedido PED-2024-015 criado - mobiliário', '2024-03-12 08:00:00'),
(9, -23.6300, -46.6700, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo GHI7J89 - Motorista Carlos Lima', '2024-03-12 08:30:00'),
(9, -23.6300, -46.6700, 'COLETA_REALIZADA', 'Carga volumosa coletada', '2024-03-12 09:00:00'),
(9, -23.6150, -46.6720, 'POSICAO_ATUALIZADA', 'Em trânsito - Av. Santo Amaro', '2024-03-12 09:30:00'),

-- PED-2024-004 (ATRIBUIDA)
(4, -23.5560, -46.6680, 'PEDIDO_CRIADO', 'Pedido PED-2024-004 criado', '2024-02-15 14:00:00'),
(4, -23.5560, -46.6680, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo ABC1D23 - Motorista João Silva', '2024-02-15 14:30:00'),

-- PED-2024-005 (PENDENTE)
(14, -23.5430, -46.6420, 'PEDIDO_CRIADO', 'Pedido PED-2024-005 criado - aguardando atribuição', '2024-02-20 08:00:00'),

-- PED-2024-006 (PENDENTE)
(15, -23.5880, -46.6350, 'PEDIDO_CRIADO', 'Pedido PED-2024-006 criado - aguardando atribuição', '2024-02-25 10:00:00'),

-- PED-2024-009 (CANCELADA)
(19, -23.6800, -46.6750, 'PEDIDO_CRIADO', 'Pedido PED-2024-009 criado', '2024-03-10 11:00:00'),
(19, -23.6800, -46.6750, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo FGH5I67', '2024-03-10 11:30:00'),
(19, -23.6800, -46.6750, 'ENTREGA_CANCELADA', 'Cancelada pelo cliente - endereço incorreto', '2024-03-10 14:00:00'),

-- PED-2024-020 (CANCELADA)
(20, -23.5530, -46.6920, 'PEDIDO_CRIADO', 'Pedido PED-2024-020 criado', '2024-03-18 10:00:00'),
(20, -23.5530, -46.6920, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo DEF4G56', '2024-03-18 10:15:00'),
(20, -23.5530, -46.6920, 'COLETA_REALIZADA', 'Pacote coletado', '2024-03-18 10:30:00'),
(20, -23.5580, -46.6870, 'POSICAO_ATUALIZADA', 'Em trânsito - Vila Madalena', '2024-03-18 11:00:00'),
(20, -23.5580, -46.6870, 'ENTREGA_CANCELADA', 'Cliente não encontrado no endereço após 3 tentativas', '2024-03-18 12:00:00');

-- Verificação
SELECT 'Entregas inseridas: ' || COUNT(*) FROM entregas;
SELECT 'Rastreamentos inseridos: ' || COUNT(*) FROM rastreamentos;
SELECT 'Por status:' AS info;
SELECT status, COUNT(*) FROM entregas GROUP BY status ORDER BY status;
