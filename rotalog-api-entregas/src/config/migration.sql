-- RotaLog - API Entregas Schema
-- Executar no schema 'entregas' do PostgreSQL

CREATE SCHEMA IF NOT EXISTS entregas;

SET search_path TO entregas;

-- Tabela de entregas
CREATE TABLE IF NOT EXISTS entregas (
    id BIGSERIAL PRIMARY KEY,
    numero_pedido VARCHAR(50) NOT NULL UNIQUE,
    veiculo_placa VARCHAR(7),
    motorista_id BIGINT,
    motorista_nome VARCHAR(100),
    veiculo_modelo VARCHAR(100),
    origem_endereco VARCHAR(255) NOT NULL,
    origem_lat DECIMAL(10, 7),
    origem_lng DECIMAL(10, 7),
    destino_endereco VARCHAR(255) NOT NULL,
    destino_lat DECIMAL(10, 7),
    destino_lng DECIMAL(10, 7),
    peso_kg DECIMAL(10, 2),
    distancia_km DECIMAL(10, 2),
    tempo_estimado_minutos INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    observacoes TEXT,
    data_criacao TIMESTAMP DEFAULT NOW(),
    data_atualizacao TIMESTAMP DEFAULT NOW(),
    data_coleta TIMESTAMP,
    data_entrega TIMESTAMP
);

-- FIXME: Sem FK para veículos e motoristas (estão em outro schema/serviço)
-- FIXME: Sem índices otimizados
-- FIXME: Sem particionamento por data

-- Tabela de rastreamento
CREATE TABLE IF NOT EXISTS rastreamentos (
    id BIGSERIAL PRIMARY KEY,
    entrega_id BIGINT NOT NULL,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    evento VARCHAR(50) NOT NULL,
    descricao TEXT,
    data_evento TIMESTAMP DEFAULT NOW()
);

-- FIXME: Sem FK constraint para entrega_id
-- FIXME: Sem índice em entrega_id
-- FIXME: Sem índice em data_evento

-- Índices básicos (adicionados depois por "outro dev")
CREATE INDEX IF NOT EXISTS idx_entregas_status ON entregas(status);
CREATE INDEX IF NOT EXISTS idx_entregas_numero_pedido ON entregas(numero_pedido);
CREATE INDEX IF NOT EXISTS idx_rastreamentos_entrega_id ON rastreamentos(entrega_id);
