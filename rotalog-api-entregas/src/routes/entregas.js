/**
 * Entregas Routes
 * 
 * FIXME: Mix de callbacks e async/await (60% callbacks)
 * FIXME: Sem validação de input com middleware
 * FIXME: Tratamento de erro inconsistente
 * FIXME: Lógica de negócio misturada nas rotas
 */

const express = require('express');
const router = express.Router();
const { Entrega, Rastreamento } = require('../models');
const { Op } = require('sequelize');
const { sequelize } = require('../config/database');

/**
 * GET /api/entregas - Listar todas as entregas
 * 
 * FIXME: Sem paginação
 * FIXME: Aceita filtro por veiculo via query param (usado pelo api-frotas)
 */
router.get('/', async (req, res) => {
    try {
        const where = {};

        // Filtro por veículo (usado pelo api-frotas)
        if (req.query.veiculo) {
            where.veiculo_placa = req.query.veiculo;
        }

        // Filtro por status
        if (req.query.status) {
            where.status = req.query.status.toUpperCase();
        }

        // Filtro por motorista
        if (req.query.motorista_id) {
            where.motorista_id = req.query.motorista_id;
        }

        const entregas = await Entrega.findAll({
            where,
            order: [['data_criacao', 'DESC']],
            include: [{
                model: Rastreamento,
                as: 'rastreamentos',
                limit: 5, // FIXME: limit hardcoded
                order: [['data_evento', 'DESC']]
            }]
        });

        res.json(entregas);
    } catch (error) {
        console.error('Erro ao listar entregas:', error.message); // FIXME: usar logger
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

/**
 * GET /api/entregas/stats - Estatísticas de entregas
 * 
 * FIXME: Query raw em vez de usar Sequelize aggregation
 * FIXME: Sem cache
 */
router.get('/stats', function(req, res) {
    // FIXME: Raw query em vez de Sequelize
    const query = `
        SELECT 
            status,
            COUNT(*) as total,
            COALESCE(AVG(distancia_km), 0) as distancia_media,
            COALESCE(AVG(peso_kg), 0) as peso_medio
        FROM entregas.entregas
        GROUP BY status
    `;

    sequelize.query(query, { type: sequelize.constructor.QueryTypes.SELECT })
        .then(function(stats) {
            const totalEntregas = stats.reduce(function(acc, s) { return acc + parseInt(s.total); }, 0);
            
            res.json({
                total: totalEntregas,
                por_status: stats,
                gerado_em: new Date().toISOString()
            });
        })
        .catch(function(error) {
            console.error('Erro ao buscar estatísticas:', error.message);
            res.status(500).json({ error: 'Erro ao buscar estatísticas' });
        });
});

/**
 * GET /api/entregas/:id - Buscar entrega por ID
 */
router.get('/:id', async (req, res) => {
    try {
        const entrega = await Entrega.findByPk(req.params.id, {
            include: [{
                model: Rastreamento,
                as: 'rastreamentos',
                order: [['data_evento', 'DESC']]
            }]
        });

        if (!entrega) {
            return res.status(404).json({ error: 'Entrega não encontrada' });
        }

        res.json(entrega);
    } catch (error) {
        console.error('Erro ao buscar entrega:', error.message);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

/**
 * GET /api/entregas/pedido/:numeroPedido - Buscar por número do pedido
 */
router.get('/pedido/:numeroPedido', function(req, res) {
    // FIXME: Callback style
    Entrega.findOne({ where: { numero_pedido: req.params.numeroPedido } })
        .then(function(entrega) {
            if (!entrega) {
                return res.status(404).json({ error: 'Pedido não encontrado' });
            }
            res.json(entrega);
        })
        .catch(function(error) {
            console.error('Erro ao buscar pedido:', error.message);
            res.status(500).json({ error: 'Erro interno do servidor' });
        });
});

/**
 * POST /api/entregas - Criar nova entrega
 * 
 * FIXME: Validação inline (deveria usar middleware)
 * FIXME: Número do pedido gerado com Math.random (não é UUID)
 * FIXME: Lógica de negócio na rota
 */
router.post('/', async (req, res) => {
    try {
        const { origem_endereco, destino_endereco, peso_kg, observacoes,
                origem_lat, origem_lng, destino_lat, destino_lng } = req.body;

        // FIXME: Validação inline
        if (!origem_endereco || !destino_endereco) {
            return res.status(400).json({ error: 'Endereços de origem e destino são obrigatórios' });
        }

        // FIXME: Geração de número de pedido com Math.random
        const numeroPedido = 'PED-' + Date.now() + '-' + Math.floor(Math.random() * 1000);

        // FIXME: Cálculo de distância fake
        let distanciaKm = null;
        if (origem_lat && origem_lng && destino_lat && destino_lng) {
            // FIXME: Fórmula simplificada - deveria usar Haversine ou API de rotas
            const dLat = Math.abs(destino_lat - origem_lat);
            const dLng = Math.abs(destino_lng - origem_lng);
            distanciaKm = Math.sqrt(dLat * dLat + dLng * dLng) * 111; // FIXME: aproximação grosseira
        }

        // FIXME: Tempo estimado hardcoded
        const tempoEstimado = distanciaKm ? Math.ceil(distanciaKm * 2) : null; // 2 min/km

        const entrega = await Entrega.create({
            numero_pedido: numeroPedido,
            origem_endereco,
            destino_endereco,
            origem_lat: origem_lat || null,
            origem_lng: origem_lng || null,
            destino_lat: destino_lat || null,
            destino_lng: destino_lng || null,
            peso_kg: peso_kg || null,
            distancia_km: distanciaKm,
            tempo_estimado_minutos: tempoEstimado,
            status: 'PENDENTE',
            observacoes: observacoes || null,
            data_criacao: new Date(),
            data_atualizacao: new Date()
        });

        // Criar evento de rastreamento inicial
        await Rastreamento.create({
            entrega_id: entrega.id,
            evento: 'PEDIDO_CRIADO',
            descricao: 'Pedido de entrega criado: ' + numeroPedido,
            data_evento: new Date()
        });

        console.log('Entrega criada:', numeroPedido); // FIXME: usar logger

        res.status(201).json(entrega);
    } catch (error) {
        console.error('Erro ao criar entrega:', error.message);
        // FIXME: Expondo detalhes do erro interno
        res.status(500).json({ error: 'Erro ao criar entrega', detalhes: error.message });
    }
});

/**
 * PUT /api/entregas/:id - Atualizar entrega
 */
router.put('/:id', async (req, res) => {
    try {
        const entrega = await Entrega.findByPk(req.params.id);
        if (!entrega) {
            return res.status(404).json({ error: 'Entrega não encontrada' });
        }

        const { origem_endereco, destino_endereco, peso_kg, observacoes } = req.body;

        // FIXME: Sem validação de campos
        if (origem_endereco) entrega.origem_endereco = origem_endereco;
        if (destino_endereco) entrega.destino_endereco = destino_endereco;
        if (peso_kg !== undefined) entrega.peso_kg = peso_kg;
        if (observacoes !== undefined) entrega.observacoes = observacoes;
        entrega.data_atualizacao = new Date();

        await entrega.save();
        res.json(entrega);
    } catch (error) {
        console.error('Erro ao atualizar entrega:', error.message);
        res.status(500).json({ error: 'Erro ao atualizar entrega' });
    }
});

/**
 * PATCH /api/entregas/:id/status - Atualizar status da entrega
 * 
 * FIXME: Sem validação de máquina de estados
 * FIXME: Sem autorização
 */
router.patch('/:id/status', async (req, res) => {
    try {
        const { status } = req.body;
        const entrega = await Entrega.findByPk(req.params.id);

        if (!entrega) {
            return res.status(404).json({ error: 'Entrega não encontrada' });
        }

        // FIXME: Validação de status fraca
        const statusValidos = ['PENDENTE', 'ATRIBUIDA', 'EM_TRANSITO', 'ENTREGUE', 'CANCELADA'];
        if (!statusValidos.includes(status)) {
            return res.status(400).json({ error: 'Status inválido', statusValidos });
        }

        // FIXME: Sem validação de transição de estado (pode ir de ENTREGUE para PENDENTE)
        const statusAnterior = entrega.status;
        entrega.status = status;
        entrega.data_atualizacao = new Date();

        if (status === 'EM_TRANSITO') {
            entrega.data_coleta = new Date();
        }
        if (status === 'ENTREGUE') {
            entrega.data_entrega = new Date();
        }

        await entrega.save();

        // Registrar evento de rastreamento
        await Rastreamento.create({
            entrega_id: entrega.id,
            evento: 'STATUS_ALTERADO',
            descricao: 'Status alterado de ' + statusAnterior + ' para ' + status,
            data_evento: new Date()
        });

        console.log('Status atualizado:', entrega.numero_pedido, statusAnterior, '->', status);

        res.json(entrega);
    } catch (error) {
        console.error('Erro ao atualizar status:', error.message);
        res.status(500).json({ error: 'Erro ao atualizar status' });
    }
});

/**
 * PATCH /api/entregas/:id/atribuir - Atribuir veículo e motorista à entrega
 * 
 * FIXME: Sem verificação se veículo/motorista existem no api-frotas
 * FIXME: Sem verificação de disponibilidade
 */
router.patch('/:id/atribuir', async (req, res) => {
    try {
        const { veiculo_placa, motorista_id, motorista_nome, veiculo_modelo } = req.body;
        const entrega = await Entrega.findByPk(req.params.id);

        if (!entrega) {
            return res.status(404).json({ error: 'Entrega não encontrada' });
        }

        if (!veiculo_placa || !motorista_id) {
            return res.status(400).json({ error: 'Placa do veículo e ID do motorista são obrigatórios' });
        }

        // FIXME: Sem validação se veículo existe no api-frotas
        // FIXME: Sem validação se motorista existe no api-frotas
        // FIXME: Sem validação de disponibilidade

        entrega.veiculo_placa = veiculo_placa;
        entrega.motorista_id = motorista_id;
        entrega.motorista_nome = motorista_nome || null;
        entrega.veiculo_modelo = veiculo_modelo || null;
        entrega.status = 'ATRIBUIDA';
        entrega.data_atualizacao = new Date();

        await entrega.save();

        // Registrar evento
        await Rastreamento.create({
            entrega_id: entrega.id,
            evento: 'ENTREGA_ATRIBUIDA',
            descricao: 'Atribuída ao veículo ' + veiculo_placa + ' e motorista #' + motorista_id,
            data_evento: new Date()
        });

        console.log('Entrega atribuída:', entrega.numero_pedido, 'veículo:', veiculo_placa);

        res.json(entrega);
    } catch (error) {
        console.error('Erro ao atribuir entrega:', error.message);
        res.status(500).json({ error: 'Erro ao atribuir entrega' });
    }
});

/**
 * DELETE /api/entregas/:id - Cancelar entrega
 * 
 * FIXME: Não deleta de verdade, apenas muda status (soft delete)
 * FIXME: Sem verificação se pode cancelar
 */
router.delete('/:id', function(req, res) {
    // FIXME: Callback style
    Entrega.findByPk(req.params.id)
        .then(function(entrega) {
            if (!entrega) {
                return res.status(404).json({ error: 'Entrega não encontrada' });
            }

            // FIXME: Sem verificação se já está entregue
            entrega.status = 'CANCELADA';
            entrega.data_atualizacao = new Date();

            return entrega.save().then(function() {
                return Rastreamento.create({
                    entrega_id: entrega.id,
                    evento: 'ENTREGA_CANCELADA',
                    descricao: 'Entrega cancelada',
                    data_evento: new Date()
                }).then(function() {
                    res.json({ message: 'Entrega cancelada', entrega: entrega });
                });
            });
        })
        .catch(function(error) {
            console.error('Erro ao cancelar entrega:', error.message);
            res.status(500).json({ error: 'Erro ao cancelar entrega' });
        });
});

module.exports = router;
