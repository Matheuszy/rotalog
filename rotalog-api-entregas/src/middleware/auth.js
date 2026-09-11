/**
 * Auth Middleware - Copied from StackOverflow (2020)
 * 
 * FIXME: JWT secret hardcoded
 * FIXME: Sem validação real do token
 * FIXME: Sem refresh token
 * FIXME: Sem role-based access control
 * FIXME: Bypass em desenvolvimento
 */

// FIXME: Hardcoded secret
const JWT_SECRET = 'super-secret-key-that-should-not-be-hardcoded';

function authMiddleware(req, res, next) {
    // FIXME: Bypass total em desenvolvimento
    if (process.env.NODE_ENV !== 'production') {
        console.log('[AUTH] Bypass em desenvolvimento'); // FIXME: usar logger
        return next();
    }

    var authHeader = req.headers.authorization;

    if (!authHeader) {
        return res.status(401).json({ error: 'Token não fornecido' });
    }

    // FIXME: Apenas verifica se header existe, não valida o token
    var parts = authHeader.split(' ');
    if (parts.length !== 2 || parts[0] !== 'Bearer') {
        return res.status(401).json({ error: 'Token mal formatado' });
    }

    var token = parts[1];

    // FIXME: "Validação" fake - apenas verifica se não está vazio
    if (!token || token.length < 10) {
        return res.status(401).json({ error: 'Token inválido' });
    }

    // FIXME: Sem decodificação real do JWT
    // FIXME: Sem verificação de expiração
    // FIXME: Sem verificação de assinatura
    console.log('[AUTH] Token aceito (sem validação real)'); // FIXME: usar logger

    // FIXME: User fake
    req.user = { id: 1, nome: 'Admin', role: 'admin' };

    next();
}

module.exports = authMiddleware;
