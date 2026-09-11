/**
 * Frotas Integration Routes
 * 
 * Endpoints proxy/wrapper para consultar dados do api-frotas
 * Usados pelo frontend para evitar CORS direto
 * 
 * FIXME: Deveria ser um BFF ou API Gateway
 * FIXME: Sem cache
 * FIXME: Sem circuit breaker
 */

const express = require('express');
const router = express.Router();
const frotasService = require('../services/frotasService');

/**
 * GET /api/frotas/veiculos/disponiveis - Listar veículos disponíveis
 * 
 * FIXME: Callback style
 */
router.get('/veiculos/disponiveis', function(req, res) {
    frotasService.listarVeiculosAtivos(function(err, veiculos) {
        if (err) {
            console.error('Erro ao buscar veículos:', err.message);
            return res.status(502).json({ 
                error: 'Erro ao comunicar com api-frotas',
                detalhes: err.message // FIXME: expondo detalhes internos
            });
        }
        res.json(veiculos);
    });
});

/**
 * GET /api/frotas/veiculos/:placa - Buscar veículo por placa
 */
router.get('/veiculos/:placa', function(req, res) {
    frotasService.buscarVeiculoPorPlaca(req.params.placa, function(err, veiculo) {
        if (err) {
            return res.status(502).json({ error: 'Erro ao comunicar com api-frotas' });
        }
        if (!veiculo) {
            return res.status(404).json({ error: 'Veículo não encontrado' });
        }
        res.json(veiculo);
    });
});

/**
 * GET /api/frotas/veiculos/:placa/disponibilidade - Verificar disponibilidade
 */
router.get('/veiculos/:placa/disponibilidade', function(req, res) {
    frotasService.verificarDisponibilidadeVeiculo(req.params.placa, function(err, resultado) {
        if (err) {
            return res.status(502).json({ error: 'Erro ao comunicar com api-frotas' });
        }
        res.json(resultado);
    });
});

/**
 * GET /api/frotas/motoristas/:id - Buscar motorista por ID
 */
router.get('/motoristas/:id', function(req, res) {
    frotasService.buscarMotoristaPorId(req.params.id, function(err, motorista) {
        if (err) {
            return res.status(502).json({ error: 'Erro ao comunicar com api-frotas' });
        }
        if (!motorista) {
            return res.status(404).json({ error: 'Motorista não encontrado' });
        }
        res.json(motorista);
    });
});

module.exports = router;
