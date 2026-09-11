/**
 * Database Configuration
 * 
 * FIXME: Credenciais hardcoded como fallback
 * FIXME: Sem connection pooling configurado adequadamente
 * FIXME: Sem retry logic na conexão
 * FIXME: Misturando Sequelize com raw queries em outros arquivos
 */

const { Sequelize } = require('sequelize');
const dotenv = require('dotenv');

dotenv.config();

// FIXME: Fallback para credenciais hardcoded se .env não existir
const DB_HOST = process.env.DB_HOST || 'localhost';
const DB_PORT = process.env.DB_PORT || 5432;
const DB_NAME = process.env.DB_NAME || 'rotalog';
const DB_USER = process.env.DB_USER || 'rotalog_admin';
const DB_PASSWORD = process.env.DB_PASSWORD || 'rotalog123'; // FIXME: senha hardcoded
const DB_SCHEMA = process.env.DB_SCHEMA || 'entregas';

const sequelize = new Sequelize(DB_NAME, DB_USER, DB_PASSWORD, {
    host: DB_HOST,
    port: DB_PORT,
    dialect: 'postgres',
    logging: console.log, // FIXME: deveria usar logger adequado
    schema: DB_SCHEMA,
    define: {
        schema: DB_SCHEMA,
        timestamps: false, // FIXME: gerenciando timestamps manualmente
        underscored: true
    },
    pool: {
        max: 5,
        min: 0,
        acquire: 30000,
        idle: 10000
    }
    // TODO: Add SSL configuration for production
    // TODO: Add read replicas
});

// FIXME: Sem retry logic
async function testConnection() {
    try {
        await sequelize.authenticate();
        console.log('Database connection established successfully.'); // FIXME: usar logger
    } catch (error) {
        console.error('Unable to connect to the database:', error.message); // FIXME: usar logger
        // FIXME: Não faz retry, apenas loga o erro
    }
}

module.exports = { sequelize, testConnection };
