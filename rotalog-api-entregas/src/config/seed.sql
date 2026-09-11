-- RotaLog - API Entregas Seed Data
-- Dados iniciais para desenvolvimento e testes

SET search_path TO entregas;

-- Entregas
INSERT INTO entregas (numero_pedido, veiculo_placa, motorista_id, origem_endereco, origem_lat, origem_lng, destino_endereco, destino_lat, destino_lng, peso_kg, distancia_km, tempo_estimado_minutos, status, observacoes, data_criacao, data_atualizacao, data_coleta, data_entrega) VALUES
('PED-2024-001', 'ABC1D23', 1, 'Rua Augusta, 1500 - São Paulo, SP', -23.5558, -46.6622, 'Av. Paulista, 1000 - São Paulo, SP', -23.5631, -46.6565, 15.50, 3.2, 15, 'ENTREGUE', 'Entrega expressa', '2024-01-15 08:00:00', '2024-01-15 10:30:00', '2024-01-15 09:00:00', '2024-01-15 10:30:00'),
('PED-2024-002', 'DEF4G56', 2, 'Av. Brasil, 500 - São Paulo, SP', -23.5505, -46.6333, 'Rua Oscar Freire, 200 - São Paulo, SP', -23.5622, -46.6720, 45.00, 8.5, 30, 'ENTREGUE', NULL, '2024-01-20 10:00:00', '2024-01-20 14:00:00', '2024-01-20 11:00:00', '2024-01-20 14:00:00'),
('PED-2024-003', 'GHI7J89', 3, 'Rua Consolação, 800 - São Paulo, SP', -23.5510, -46.6600, 'Av. Faria Lima, 3000 - São Paulo, SP', -23.5874, -46.6814, 22.30, 6.1, 25, 'EM_TRANSITO', 'Frágil - manusear com cuidado', '2024-02-10 09:00:00', '2024-02-10 11:00:00', '2024-02-10 10:30:00', NULL),
('PED-2024-004', 'ABC1D23', 1, 'Rua Haddock Lobo, 400 - São Paulo, SP', -23.5560, -46.6680, 'Av. Rebouças, 1200 - São Paulo, SP', -23.5640, -46.6730, 8.00, 2.5, 12, 'ATRIBUIDA', NULL, '2024-02-15 14:00:00', '2024-02-15 14:30:00', NULL, NULL),
('PED-2024-005', NULL, NULL, 'Av. Ipiranga, 200 - São Paulo, SP', -23.5430, -46.6420, 'Rua da Mooca, 1500 - São Paulo, SP', -23.5580, -46.6010, 120.00, 12.3, 40, 'PENDENTE', 'Carga pesada - necessita veículo grande', '2024-02-20 08:00:00', '2024-02-20 08:00:00', NULL, NULL),
('PED-2024-006', NULL, NULL, 'Rua Vergueiro, 3000 - São Paulo, SP', -23.5880, -46.6350, 'Av. Santo Amaro, 500 - São Paulo, SP', -23.6200, -46.6650, 35.00, 9.8, 35, 'PENDENTE', NULL, '2024-02-25 10:00:00', '2024-02-25 10:00:00', NULL, NULL),
('PED-2024-007', 'VWX9Y01', 5, 'Av. Brigadeiro Faria Lima, 1000 - São Paulo, SP', -23.5740, -46.6890, 'Rua Funchal, 400 - São Paulo, SP', -23.5860, -46.6930, 5.00, 1.8, 10, 'EM_TRANSITO', 'Documento urgente', '2024-03-01 07:00:00', '2024-03-01 08:00:00', '2024-03-01 07:30:00', NULL),
('PED-2024-008', 'BCD2E34', 7, 'Rua Bela Cintra, 700 - São Paulo, SP', -23.5570, -46.6660, 'Av. Angélica, 2000 - São Paulo, SP', -23.5430, -46.6580, 18.50, 4.2, 18, 'ATRIBUIDA', NULL, '2024-03-05 09:00:00', '2024-03-05 10:00:00', NULL, NULL),
('PED-2024-009', 'FGH5I67', 3, 'Av. Interlagos, 5000 - São Paulo, SP', -23.6800, -46.6750, 'Rua Domingos de Morais, 800 - São Paulo, SP', -23.5980, -46.6380, 65.00, 15.5, 50, 'CANCELADA', 'Cancelado pelo cliente', '2024-03-10 11:00:00', '2024-03-10 14:00:00', NULL, NULL),
('PED-2024-010', NULL, NULL, 'Rua Teodoro Sampaio, 1200 - São Paulo, SP', -23.5530, -46.6810, 'Av. Sumaré, 300 - São Paulo, SP', -23.5350, -46.6780, 28.00, 3.5, 15, 'PENDENTE', 'Agendar para período da manhã', '2024-03-15 08:00:00', '2024-03-15 08:00:00', NULL, NULL);

-- Rastreamentos
INSERT INTO rastreamentos (entrega_id, latitude, longitude, evento, descricao, data_evento) VALUES
(1, -23.5558, -46.6622, 'PEDIDO_CRIADO', 'Pedido de entrega criado: PED-2024-001', '2024-01-15 08:00:00'),
(1, -23.5558, -46.6622, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo ABC1D23 e motorista #1', '2024-01-15 08:30:00'),
(1, -23.5558, -46.6622, 'STATUS_ALTERADO', 'Status alterado de ATRIBUIDA para EM_TRANSITO', '2024-01-15 09:00:00'),
(1, -23.5590, -46.6590, 'POSICAO_ATUALIZADA', 'Em trânsito - Rua da Consolação', '2024-01-15 09:30:00'),
(1, -23.5631, -46.6565, 'STATUS_ALTERADO', 'Status alterado de EM_TRANSITO para ENTREGUE', '2024-01-15 10:30:00'),
(2, -23.5505, -46.6333, 'PEDIDO_CRIADO', 'Pedido de entrega criado: PED-2024-002', '2024-01-20 10:00:00'),
(2, -23.5505, -46.6333, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo DEF4G56 e motorista #2', '2024-01-20 10:30:00'),
(2, -23.5622, -46.6720, 'STATUS_ALTERADO', 'Status alterado de EM_TRANSITO para ENTREGUE', '2024-01-20 14:00:00'),
(3, -23.5510, -46.6600, 'PEDIDO_CRIADO', 'Pedido de entrega criado: PED-2024-003', '2024-02-10 09:00:00'),
(3, -23.5510, -46.6600, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo GHI7J89 e motorista #3', '2024-02-10 09:30:00'),
(3, -23.5650, -46.6700, 'POSICAO_ATUALIZADA', 'Em trânsito - Av. Rebouças', '2024-02-10 11:00:00'),
(5, -23.5430, -46.6420, 'PEDIDO_CRIADO', 'Pedido de entrega criado: PED-2024-005', '2024-02-20 08:00:00'),
(7, -23.5740, -46.6890, 'PEDIDO_CRIADO', 'Pedido de entrega criado: PED-2024-007', '2024-03-01 07:00:00'),
(7, -23.5740, -46.6890, 'ENTREGA_ATRIBUIDA', 'Atribuída ao veículo VWX9Y01 e motorista #5', '2024-03-01 07:15:00'),
(7, -23.5800, -46.6910, 'POSICAO_ATUALIZADA', 'Em trânsito - Av. Faria Lima', '2024-03-01 07:45:00'),
(9, -23.6800, -46.6750, 'PEDIDO_CRIADO', 'Pedido de entrega criado: PED-2024-009', '2024-03-10 11:00:00'),
(9, -23.6800, -46.6750, 'ENTREGA_CANCELADA', 'Entrega cancelada pelo cliente', '2024-03-10 14:00:00');

-- FIXME: Dados de seed não são idempotentes
-- FIXME: Sem verificação de duplicidade
-- TODO: Adicionar mais dados para testes de performance
