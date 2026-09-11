/**
 * Models Index
 * 
 * FIXME: Sem associações definidas entre models
 * FIXME: Sem validações de integridade referencial
 */

const Entrega = require('./Entrega');
const Rastreamento = require('./Rastreamento');

// FIXME: Associações definidas aqui em vez de nos models (inconsistência)
Entrega.hasMany(Rastreamento, { foreignKey: 'entrega_id', as: 'rastreamentos' });
Rastreamento.belongsTo(Entrega, { foreignKey: 'entrega_id', as: 'entrega' });

module.exports = { Entrega, Rastreamento };
