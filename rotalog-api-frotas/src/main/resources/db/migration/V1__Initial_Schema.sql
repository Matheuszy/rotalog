-- RotaLog - API Frotas Initial Schema
-- Legacy schema with intentional debt

CREATE TABLE IF NOT EXISTS veiculos (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(7) UNIQUE NOT NULL,
    modelo VARCHAR(100),
    ano_fabricacao INTEGER,
    quilometragem BIGINT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ATIVO',
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TODO: Add indexes
-- TODO: Add constraints
-- TODO: Add triggers
-- TODO: Add partitioning for large tables

CREATE TABLE IF NOT EXISTS motoristas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cnh VARCHAR(11) UNIQUE NOT NULL,
    categoria_cnh VARCHAR(5),
    vencimento_cnh DATE,
    status VARCHAR(20) DEFAULT 'ATIVO',
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TODO: Add foreign key constraints
-- TODO: Add check constraints
-- TODO: Add default values

CREATE TABLE IF NOT EXISTS manutencoes (
    id BIGSERIAL PRIMARY KEY,
    veiculo_id BIGINT,
    tipo_manutencao VARCHAR(50),
    data_manutencao TIMESTAMP,
    quilometragem_manutencao BIGINT,
    custo DECIMAL(10, 2),
    descricao TEXT,
    status VARCHAR(20) DEFAULT 'PENDENTE',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TODO: Add indexes for performance
-- TODO: Add partitioning by date
-- TODO: Add archival strategy

-- FIXME: Missing audit tables
-- FIXME: Missing event sourcing tables
-- FIXME: Missing cache invalidation mechanism
