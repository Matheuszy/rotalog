/**
 * Error Handler Middleware
 * 
 * FIXME: Expõe stack trace em todos os ambientes
 * FIXME: Sem logging estruturado
 * FIXME: Sem métricas de erro
 */

function errorHandler(err, req, res, next) {
    console.error('=== ERRO NÃO TRATADO ==='); // FIXME: usar logger
    console.error('URL:', req.method, req.url);
    console.error('Body:', JSON.stringify(req.body)); // FIXME: pode logar dados sensíveis
    console.error('Erro:', err.message);
    console.error('Stack:', err.stack); // FIXME: stack trace no log
    console.error('========================');

    // FIXME: Expondo stack trace para o cliente
    res.status(err.status || 500).json({
        error: err.message || 'Erro interno do servidor',
        stack: process.env.NODE_ENV !== 'production' ? err.stack : undefined // FIXME: deveria nunca expor
    });
}

function notFoundHandler(req, res) {
    res.status(404).json({
        error: 'Rota não encontrada',
        path: req.path,
        method: req.method
    });
}

module.exports = { errorHandler, notFoundHandler };
