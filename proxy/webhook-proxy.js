const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();

app.use('/webhook', createProxyMiddleware({
  target: 'http://localhost:8080',
  changeOrigin: true,
  pathRewrite: { '^/webhook': '/webhook' },
  onProxyReq: (proxyReq, req, res) => {
    console.log(`[>] Encaminhando para ${proxyReq.path}`);
  },
  onError: (err, req, res) => {
    console.error('[x] Erro no proxy:', err);
    res.status(500).send('Erro interno no proxy.');
  },
}));

const PORT = 3003;
app.listen(PORT, () => {
  console.log(`[*] Proxy rodando em http://localhost:${PORT}/webhook`);
});
