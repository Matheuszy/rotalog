/**
 * Rastreamento Model - Tracking events for deliveries
 * 
 * FIXME: Sem índices para queries frequentes
 * FIXME: Sem particionamento por data
 */

const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/database');

const Rastreamento = sequelize.define('Rastreamento', {
    id: {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true
    },
    entrega_id: {
        type: DataTypes.BIGINT,
        allowNull: false
        // FIXME: sem FK constraint no banco
    },
    latitude: {
        type: DataTypes.DECIMAL(10, 7),
        allowNull: true
    },
    longitude: {
        type: DataTypes.DECIMAL(10, 7),
        allowNull: true
    },
    evento: {
        type: DataTypes.STRING(50),
        allowNull: false
        // FIXME: deveria ser ENUM
    },
    descricao: {
        type: DataTypes.TEXT,
        allowNull: true
    },
    data_evento: {
        type: DataTypes.DATE,
        defaultValue: DataTypes.NOW
    }
}, {
    tableName: 'rastreamentos',
    timestamps: false
});

module.exports = Rastreamento;
