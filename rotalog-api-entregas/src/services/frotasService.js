/**
 * Frotas Service - Integração HTTP com api-frotas (Java/Spring Boot)
 * 
 * FIXME: URL hardcoded
 * FIXME: Sem circuit breaker
 * FIXME: Sem retry logic
 * FIXME: Sem timeout configurável
 * FIXME: Usando http nativo em vez de axios
 */

const http = require('http');

// FIXME: URL hardcoded - deveria vir de config
const API_FROTAS_URL = process.env.API_FROTAS_URL || 'http://localhost:8080/api';

/**
 * Buscar veículo por placa no api-frotas
 * 
 * FIXME: Callback style com http nativo
 * FIXME: Sem tratamento de timeout
 */
function buscarVeiculoPorPlaca(placa, callback) {
    const url = API_FROTAS_URL + '/veiculos/placa/' + placa;
    console.log('Buscando veículo no api-frotas:', url); // FIXME: usar logger

    http.get(url, function(response) {
        var data = '';

        response.on('data', function(chunk) {
            data += chunk;
        });

        response.on('end', function() {
            try {
                if (response.statusCode === 200) {
                    var veiculo = JSON.parse(data);
                    callback(null, veiculo);
                } else if (response.statusCode === 404) {
                    callback(null, null);
                } else {
                    callback(new Error('Erro ao buscar veículo: HTTP ' + response.statusCode));
                }
            } catch (e) {
                callback(new Error('Erro ao parsear resposta: ' + e.message));
            }
        });
    }).on('error', function(error) {
        console.error('Erro na conexão com api-frotas:', error.message);
        callback(error);
    });
}

/**
 * Buscar motorista por ID no api-frotas
 * 
 * FIXME: Callback style
 */
function buscarMotoristaPorId(id, callback) {
    const url = API_FROTAS_URL + '/motoristas/' + id;
    console.log('Buscando motorista no api-frotas:', url);

    http.get(url, function(response) {
        var data = '';

        response.on('data', function(chunk) {
            data += chunk;
        });

        response.on('end', function() {
            try {
                if (response.statusCode === 200) {
                    var motorista = JSON.parse(data);
                    callback(null, motorista);
                } else if (response.statusCode === 404) {
                    callback(null, null);
                } else {
                    callback(new Error('Erro ao buscar motorista: HTTP ' + response.statusCode));
                }
            } catch (e) {
                callback(new Error('Erro ao parsear resposta: ' + e.message));
            }
        });
    }).on('error', function(error) {
        console.error('Erro na conexão com api-frotas:', error.message);
        callback(error);
    });
}

/**
 * Verificar se veículo está disponível (ATIVO e sem manutenção)
 * 
 * FIXME: Lógica duplicada - api-frotas já tem essa verificação
 */
function verificarDisponibilidadeVeiculo(placa, callback) {
    buscarVeiculoPorPlaca(placa, function(err, veiculo) {
        if (err) {
            return callback(err);
        }
        if (!veiculo) {
            return callback(null, { disponivel: false, motivo: 'Veículo não encontrado' });
        }
        if (veiculo.status !== 'ATIVO') {
            return callback(null, { disponivel: false, motivo: 'Veículo com status: ' + veiculo.status });
        }
        callback(null, { disponivel: true, veiculo: veiculo });
    });
}

/**
 * Listar veículos ativos
 * 
 * FIXME: Callback style com http nativo
 */
function listarVeiculosAtivos(callback) {
    const url = API_FROTAS_URL + '/veiculos/status/ATIVO';

    http.get(url, function(response) {
        var data = '';

        response.on('data', function(chunk) {
            data += chunk;
        });

        response.on('end', function() {
            try {
                if (response.statusCode === 200) {
                    callback(null, JSON.parse(data));
                } else {
                    callback(new Error('Erro ao listar veículos: HTTP ' + response.statusCode));
                }
            } catch (e) {
                callback(new Error('Erro ao parsear resposta: ' + e.message));
            }
        });
    }).on('error', function(error) {
        console.error('Erro na conexão com api-frotas:', error.message);
        callback(error);
    });
}

module.exports = {
    buscarVeiculoPorPlaca,
    buscarMotoristaPorId,
    verificarDisponibilidadeVeiculo,
    listarVeiculosAtivos
};
