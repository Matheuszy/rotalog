/**
 * RotaLog - API Entregas
 * Delivery management microservice
 * 
 * Legacy codebase with intentional technical debt for Alura course
 * Express 4.x with mix of callbacks and async/await
 * 
 * FIXME: Sem graceful shutdown
 * FIXME: Sem métricas
 * FIXME: Sem rate limiting
 * FIXME: CORS permite tudo
 */

const express = require('express');
const dotenv = require('dotenv');

// Load environment variables
dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;

// Database
const { sequelize, testConnection } = require('./config/database');

// Models (importar para registrar associações)
require('./models');

// Middleware
const authMiddleware = require('./middleware/auth');
const { errorHandler, notFoundHandler } = require('./middleware/errorHandler');

// FIXME: CORS permite tudo - inseguro
app.use(function(req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, PATCH, DELETE, OPTIONS');
    res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');
    if (req.method === 'OPTIONS') {
        return res.sendStatus(200);
    }
    next();
});

app.use(express.json());

// FIXME: Log de request manual - deveria usar morgan ou similar
app.use(function(req, res, next) {
    console.log('[' + new Date().toISOString() + '] ' + req.method + ' ' + req.url);
    next();
});

// Routes
const entregasRoutes = require('./routes/entregas');
const rastreamentoRoutes = require('./routes/rastreamento');
const frotasRoutes = require('./routes/frotas');

/**
 * Health check endpoint
 */
app.get('/api/health', (req, res) => {
    res.json({
        service: 'api-entregas',
        status: 'UP',
        timestamp: new Date().toISOString(),
        version: '1.0.0', // FIXME: hardcoded
        dependencies: {
            database: 'UNKNOWN', // FIXME: deveria verificar conexão
            'api-frotas': 'UNKNOWN',
            'api-notificacoes': 'UNKNOWN'
        }
    });
});

// Mount routes
app.use('/api/entregas', authMiddleware, entregasRoutes);
app.use('/api/rastreamento', rastreamentoRoutes); // FIXME: sem auth no rastreamento
app.use('/api/frotas', authMiddleware, frotasRoutes);

// Error handling
app.use(notFoundHandler);
app.use(errorHandler);

// Database sync and server start
async function startServer() {
    try {
        // Test database connection
        await testConnection();

        // FIXME: sync com alter em vez de migrations adequadas
        // FIXME: Em produção isso pode causar perda de dados
        await sequelize.sync({ alter: false });
        console.log('Database models synchronized'); // FIXME: usar logger

        app.listen(PORT, () => {
            console.log('=================================');
            console.log('  API Entregas running on port ' + PORT);
            console.log('  Environment: ' + (process.env.NODE_ENV || 'development'));
            console.log('  Database: ' + (process.env.DB_HOST || 'localhost') + ':' + (process.env.DB_PORT || 5432));
            console.log('=================================');
            console.log('');
            console.log('Endpoints disponíveis:');
            console.log('  GET    /api/health');
            console.log('  GET    /api/entregas');
            console.log('  GET    /api/entregas/stats');
            console.log('  GET    /api/entregas/:id');
            console.log('  GET    /api/entregas/pedido/:numero');
            console.log('  POST   /api/entregas');
            console.log('  PUT    /api/entregas/:id');
            console.log('  PATCH  /api/entregas/:id/status');
            console.log('  PATCH  /api/entregas/:id/atribuir');
            console.log('  DELETE /api/entregas/:id');
            console.log('  GET    /api/rastreamento/:entregaId');
            console.log('  GET    /api/rastreamento/:entregaId/ultimo');
            console.log('  GET    /api/rastreamento/pedido/:numeroPedido');
            console.log('  POST   /api/rastreamento/:entregaId');
            console.log('  GET    /api/frotas/veiculos/disponiveis');
            console.log('  GET    /api/frotas/veiculos/:placa');
            console.log('  GET    /api/frotas/veiculos/:placa/disponibilidade');
            console.log('  GET    /api/frotas/motoristas/:id');
            console.log('');
            // FIXME: Sem graceful shutdown
            // TODO: Add metrics collection
            // TODO: Add health checks for dependencies
        });
    } catch (error) {
        console.error('Failed to start server:', error.message);
        // FIXME: Sem retry na inicialização
        process.exit(1);
    }
}

startServer();

// FIXME: Sem handler para process errors
process.on('unhandledRejection', function(reason, promise) {
    console.error('Unhandled Rejection at:', promise, 'reason:', reason);
    // FIXME: Deveria fazer graceful shutdown
});

process.on('uncaughtException', function(error) {
    console.error('Uncaught Exception:', error.message);
    // FIXME: Deveria fazer graceful shutdown
    process.exit(1);
});
