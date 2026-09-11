/**
 * Entrega Model
 * 
 * FIXME: Sem validações adequadas no model
 * FIXME: Status como STRING em vez de ENUM
 * FIXME: Sem hooks para auditoria
 * FIXME: Sem índices definidos
 */

const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/database');

const Entrega = sequelize.define('Entrega', {
    id: {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true
    },
    numero_pedido: {
        type: DataTypes.STRING(50),
        allowNull: false,
        unique: true
    },
    veiculo_placa: {
        type: DataTypes.STRING(7),
        allowNull: true // FIXME: deveria ser obrigatório quando em trânsito
    },
    motorista_id: {
        type: DataTypes.BIGINT,
        allowNull: true // FIXME: deveria ser obrigatório quando em trânsito
    },
    motorista_nome: {
        type: DataTypes.STRING(100),
        allowNull: true
        // FIXME: campo denormalizado - deveria vir do api-frotas
    },
    veiculo_modelo: {
        type: DataTypes.STRING(100),
        allowNull: true
        // FIXME: campo denormalizado - deveria vir do api-frotas
    },
    origem_endereco: {
        type: DataTypes.STRING(255),
        allowNull: false
    },
    origem_lat: {
        type: DataTypes.DECIMAL(10, 7),
        allowNull: true
    },
    origem_lng: {
        type: DataTypes.DECIMAL(10, 7),
        allowNull: true
    },
    destino_endereco: {
        type: DataTypes.STRING(255),
        allowNull: false
    },
    destino_lat: {
        type: DataTypes.DECIMAL(10, 7),
        allowNull: true
    },
    destino_lng: {
        type: DataTypes.DECIMAL(10, 7),
        allowNull: true
    },
    peso_kg: {
        type: DataTypes.DECIMAL(10, 2),
        allowNull: true
    },
    distancia_km: {
        type: DataTypes.DECIMAL(10, 2),
        allowNull: true
    },
    tempo_estimado_minutos: {
        type: DataTypes.INTEGER,
        allowNull: true
    },
    status: {
        type: DataTypes.STRING(20),
        allowNull: false,
        defaultValue: 'PENDENTE'
        // FIXME: deveria ser ENUM('PENDENTE', 'ATRIBUIDA', 'EM_TRANSITO', 'ENTREGUE', 'CANCELADA')
    },
    observacoes: {
        type: DataTypes.TEXT,
        allowNull: true
    },
    data_criacao: {
        type: DataTypes.DATE,
        defaultValue: DataTypes.NOW
    },
    data_atualizacao: {
        type: DataTypes.DATE,
        defaultValue: DataTypes.NOW
    },
    data_coleta: {
        type: DataTypes.DATE,
        allowNull: true
    },
    data_entrega: {
        type: DataTypes.DATE,
        allowNull: true
    }
}, {
    tableName: 'entregas',
    timestamps: false // FIXME: gerenciando timestamps manualmente
});

module.exports = Entrega;
