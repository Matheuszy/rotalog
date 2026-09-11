/**
 * Notificacao Service - Integração HTTP com api-notificacoes (.NET)
 * 
 * FIXME: URL hardcoded
 * FIXME: Sem circuit breaker
 * FIXME: Sem retry logic
 * FIXME: Fire and forget (não espera resposta)
 */

const http = require('http');

// FIXME: URL hardcoded
const API_NOTIFICACOES_URL = process.env.API_NOTIFICACOES_URL || 'http://localhost:5000';

/**
 * Enviar notificação via api-notificacoes
 * 
 * FIXME: Callback style com http nativo
 * FIXME: Sem retry
 * FIXME: Sem dead letter queue
 */
function enviarNotificacao(tipo, destinatario, mensagem, callback) {
    var body = JSON.stringify({
        tipo: tipo,
        destinatario: destinatario,
        mensagem: mensagem,
        canal: 'email' // FIXME: canal hardcoded
    });

    var urlParts = new URL(API_NOTIFICACOES_URL + '/api/notificacoes');

    var options = {
        hostname: urlParts.hostname,
        port: urlParts.port,
        path: urlParts.pathname,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Content-Length': Buffer.byteLength(body)
        }
    };

    console.log('Enviando notificação:', tipo, destinatario); // FIXME: usar logger

    var req = http.request(options, function(response) {
        var data = '';

        response.on('data', function(chunk) {
            data += chunk;
        });

        response.on('end', function() {
            if (response.statusCode >= 200 && response.statusCode < 300) {
                console.log('Notificação enviada com sucesso:', tipo);
                if (callback) callback(null, data);
            } else {
                console.error('Erro ao enviar notificação: HTTP', response.statusCode);
                if (callback) callback(new Error('HTTP ' + response.statusCode));
            }
        });
    });

    req.on('error', function(error) {
        console.error('Erro na conexão com api-notificacoes:', error.message);
        // FIXME: Engolindo erro - fire and forget
        if (callback) callback(error);
    });

    req.write(body);
    req.end();
}

/**
 * Notificar criação de entrega
 */
function notificarEntregaCriada(entrega) {
    enviarNotificacao(
        'ENTREGA_CRIADA',
        'operacao@rotalog.com',
        'Nova entrega criada: ' + entrega.numero_pedido + ' - De: ' + entrega.origem_endereco + ' Para: ' + entrega.destino_endereco,
        function(err) {
            if (err) console.error('Falha ao notificar criação de entrega:', err.message);
        }
    );
}

/**
 * Notificar mudança de status
 */
function notificarMudancaStatus(entrega, statusAnterior, statusNovo) {
    enviarNotificacao(
        'STATUS_ENTREGA',
        'operacao@rotalog.com',
        'Entrega ' + entrega.numero_pedido + ' mudou de ' + statusAnterior + ' para ' + statusNovo,
        function(err) {
            if (err) console.error('Falha ao notificar mudança de status:', err.message);
        }
    );
}

/**
 * Notificar entrega concluída
 */
function notificarEntregaConcluida(entrega) {
    enviarNotificacao(
        'ENTREGA_CONCLUIDA',
        'cliente@rotalog.com', // FIXME: deveria ser o email do cliente real
        'Sua entrega ' + entrega.numero_pedido + ' foi entregue com sucesso!',
        function(err) {
            if (err) console.error('Falha ao notificar entrega concluída:', err.message);
        }
    );
}

/**
 * Notificar atraso na entrega
 */
function notificarAtraso(entrega) {
    enviarNotificacao(
        'ENTREGA_ATRASADA',
        'gestor@rotalog.com',
        'Entrega ' + entrega.numero_pedido + ' está atrasada. Destino: ' + entrega.destino_endereco,
        function(err) {
            if (err) console.error('Falha ao notificar atraso:', err.message);
        }
    );
}

module.exports = {
    enviarNotificacao,
    notificarEntregaCriada,
    notificarMudancaStatus,
    notificarEntregaConcluida,
    notificarAtraso
};
