/**
 * Rastreamento Routes - Tracking endpoints
 * 
 * FIXME: Sem autenticação
 * FIXME: Sem rate limiting (endpoint público)
 * FIXME: Sem cache para consultas frequentes
 */

const express = require('express');
const router = express.Router();
const { Entrega, Rastreamento } = require('../models');

/**
 * GET /api/rastreamento/:entregaId - Listar eventos de rastreamento
 */
router.get('/:entregaId', async (req, res) => {
    try {
        const eventos = await Rastreamento.findAll({
            where: { entrega_id: req.params.entregaId },
            order: [['data_evento', 'DESC']]
        });

        if (eventos.length === 0) {
            return res.status(404).json({ error: 'Nenhum evento de rastreamento encontrado' });
        }

        res.json(eventos);
    } catch (error) {
        console.error('Erro ao buscar rastreamento:', error.message);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

/**
 * POST /api/rastreamento/:entregaId - Registrar evento de rastreamento
 * 
 * FIXME: Sem validação se entrega existe
 * FIXME: Sem validação de coordenadas
 */
router.post('/:entregaId', function(req, res) {
    // FIXME: Callback style
    var entregaId = req.params.entregaId;
    var body = req.body;

    Entrega.findByPk(entregaId)
        .then(function(entrega) {
            if (!entrega) {
                return res.status(404).json({ error: 'Entrega não encontrada' });
            }

            return Rastreamento.create({
                entrega_id: entregaId,
                latitude: body.latitude || null,
                longitude: body.longitude || null,
                evento: body.evento || 'POSICAO_ATUALIZADA',
                descricao: body.descricao || null,
                data_evento: new Date()
            }).then(function(evento) {
                console.log('Evento registrado:', entregaId, body.evento); // FIXME: usar logger
                res.status(201).json(evento);
            });
        })
        .catch(function(error) {
            console.error('Erro ao registrar evento:', error.message);
            res.status(500).json({ error: 'Erro ao registrar evento' });
        });
});

/**
 * GET /api/rastreamento/:entregaId/ultimo - Último evento de rastreamento
 */
router.get('/:entregaId/ultimo', async (req, res) => {
    try {
        const evento = await Rastreamento.findOne({
            where: { entrega_id: req.params.entregaId },
            order: [['data_evento', 'DESC']]
        });

        if (!evento) {
            return res.status(404).json({ error: 'Nenhum evento encontrado' });
        }

        res.json(evento);
    } catch (error) {
        console.error('Erro ao buscar último evento:', error.message);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

/**
 * GET /api/rastreamento/pedido/:numeroPedido - Rastreamento por número do pedido
 * 
 * FIXME: Endpoint público sem rate limiting
 * FIXME: Sem cache
 */
router.get('/pedido/:numeroPedido', function(req, res) {
    // FIXME: Callback style com nested queries
    Entrega.findOne({ where: { numero_pedido: req.params.numeroPedido } })
        .then(function(entrega) {
            if (!entrega) {
                return res.status(404).json({ error: 'Pedido não encontrado' });
            }

            return Rastreamento.findAll({
                where: { entrega_id: entrega.id },
                order: [['data_evento', 'DESC']]
            }).then(function(eventos) {
                res.json({
                    pedido: entrega.numero_pedido,
                    status: entrega.status,
                    origem: entrega.origem_endereco,
                    destino: entrega.destino_endereco,
                    data_criacao: entrega.data_criacao,
                    data_entrega: entrega.data_entrega,
                    eventos: eventos
                });
            });
        })
        .catch(function(error) {
            console.error('Erro ao buscar rastreamento por pedido:', error.message);
            res.status(500).json({ error: 'Erro interno do servidor' });
        });
});

module.exports = router;
